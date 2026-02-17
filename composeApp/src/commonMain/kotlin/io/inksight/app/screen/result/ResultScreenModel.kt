package io.inksight.app.screen.result

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.inksight.core.domain.model.Scan
import io.inksight.core.domain.repository.ScanRepository
import io.inksight.core.domain.usecase.UpdateTranscriptionUseCase
import io.inksight.core.platform.ShareManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResultUiState(
    val scan: Scan? = null,
    val editedText: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val showCopied: Boolean = false,
)

class ResultScreenModel(
    private val scanId: String,
    private val scanRepository: ScanRepository,
    private val updateTranscriptionUseCase: UpdateTranscriptionUseCase,
    private val shareManager: ShareManager,
) : ScreenModel {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        loadScan()
    }

    private fun loadScan() {
        screenModelScope.launch {
            scanRepository.getScanById(scanId).collect { scan ->
                if (scan != null) {
                    _uiState.value = _uiState.value.copy(
                        scan = scan,
                        editedText = if (!_uiState.value.isEditing) scan.transcribedText else _uiState.value.editedText,
                    )
                }
            }
        }
    }

    fun onTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(editedText = text)
    }

    fun toggleEditing() {
        val state = _uiState.value
        if (state.isEditing) {
            // Save changes
            saveEditedText()
        } else {
            _uiState.value = state.copy(
                isEditing = true,
                editedText = state.scan?.transcribedText ?: "",
            )
        }
    }

    private fun saveEditedText() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                updateTranscriptionUseCase(scanId, _uiState.value.editedText)
                _uiState.value = _uiState.value.copy(
                    isEditing = false,
                    isSaving = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }

    fun shareText() {
        val text = _uiState.value.scan?.transcribedText ?: return
        shareManager.shareText(text, "InkSight Transcription")
    }

    fun showCopiedFeedback() {
        _uiState.value = _uiState.value.copy(showCopied = true)
        screenModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(showCopied = false)
        }
    }
}
