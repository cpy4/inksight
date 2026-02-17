package io.inksight.app.screen.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.inksight.core.domain.model.TranscriptionResult
import io.inksight.core.domain.repository.SettingsRepository
import io.inksight.core.domain.usecase.TranscribeImageUseCase
import io.inksight.core.platform.FileManager
import io.inksight.core.platform.ImageProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class HomeUiState(
    val isProcessing: Boolean = false,
    val showApiKeyPrompt: Boolean = false,
    val error: String? = null,
    val completedScanId: String? = null,
)

class HomeScreenModel(
    private val transcribeImageUseCase: TranscribeImageUseCase,
    private val settingsRepository: SettingsRepository,
    private val imageProcessor: ImageProcessor,
    private val fileManager: FileManager,
) : ScreenModel {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun checkApiKey() {
        screenModelScope.launch {
            val hasKey = settingsRepository.hasApiKey()
            _uiState.value = _uiState.value.copy(showApiKeyPrompt = !hasKey)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onImageCaptured(imageBytes: ByteArray, mimeType: String) {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessing = true,
                error = null,
                completedScanId = null,
            )

            try {
                // Check API key
                if (!settingsRepository.hasApiKey()) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        showApiKeyPrompt = true,
                    )
                    return@launch
                }

                // Process image
                val settings = settingsRepository.getSettings().first()
                val processed = imageProcessor.processForUpload(
                    imageBytes = imageBytes,
                    maxEdge = settings.maxImageLongestEdge,
                    jpegQuality = settings.imageQuality.jpegQuality,
                )

                // Save image to local storage
                val scanId = Uuid.random().toString()
                val imagePath = fileManager.saveImage(
                    bytes = processed.bytes,
                    fileName = "scan_${scanId}.jpg",
                )

                // Transcribe
                val result = transcribeImageUseCase(
                    scanId = scanId,
                    imageBytes = processed.bytes,
                    mimeType = "image/jpeg",
                    imagePath = imagePath,
                )

                when (result) {
                    is TranscriptionResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            completedScanId = scanId,
                        )
                    }
                    is TranscriptionResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            error = result.message,
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "An unexpected error occurred",
                )
            }
        }
    }

    fun clearNavigation() {
        _uiState.value = _uiState.value.copy(completedScanId = null)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissApiKeyPrompt() {
        _uiState.value = _uiState.value.copy(showApiKeyPrompt = false)
    }
}
