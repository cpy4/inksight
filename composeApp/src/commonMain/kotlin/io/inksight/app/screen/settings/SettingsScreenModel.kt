package io.inksight.app.screen.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.inksight.core.domain.model.AppSettings
import io.inksight.core.domain.model.ImageQuality
import io.inksight.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val apiKeyInput: String = "",
    val isApiKeySaved: Boolean = false,
    val isSavingKey: Boolean = false,
    val error: String? = null,
)

class SettingsScreenModel(
    private val settingsRepository: SettingsRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        screenModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    settings = settings,
                    isApiKeySaved = settings.hasApiKey,
                )
            }
        }
    }

    fun onApiKeyInputChanged(key: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = key)
    }

    fun saveApiKey() {
        val key = _uiState.value.apiKeyInput.trim()
        if (key.isBlank()) return

        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingKey = true, error = null)
            try {
                if (!key.startsWith("sk-ant-")) {
                    _uiState.value = _uiState.value.copy(
                        isSavingKey = false,
                        error = "Invalid API key format. Key should start with 'sk-ant-'",
                    )
                    return@launch
                }
                settingsRepository.saveApiKey(key)
                _uiState.value = _uiState.value.copy(
                    isSavingKey = false,
                    isApiKeySaved = true,
                    apiKeyInput = "",
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSavingKey = false,
                    error = e.message ?: "Failed to save API key",
                )
            }
        }
    }

    fun clearApiKey() {
        screenModelScope.launch {
            settingsRepository.clearApiKey()
            _uiState.value = _uiState.value.copy(
                isApiKeySaved = false,
                apiKeyInput = "",
            )
        }
    }

    fun setImageQuality(quality: ImageQuality) {
        screenModelScope.launch {
            settingsRepository.saveImageQuality(quality)
        }
    }

    fun setDarkMode(enabled: Boolean?) {
        screenModelScope.launch {
            settingsRepository.saveDarkMode(enabled)
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
