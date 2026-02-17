package io.inksight.core.data.repository

import com.russhwolf.settings.Settings
import io.inksight.core.domain.model.AppSettings
import io.inksight.core.domain.model.ImageQuality
import io.inksight.core.domain.repository.SettingsRepository
import io.inksight.core.platform.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val settings: Settings,
) : SettingsRepository {

    private val _settingsFlow = MutableStateFlow(loadSettings())

    override fun getSettings(): Flow<AppSettings> = _settingsFlow.asStateFlow()

    override suspend fun saveApiKey(key: String) {
        secureStorage.saveString(API_KEY_KEY, key)
        refreshSettings()
    }

    override suspend fun getApiKey(): String? {
        return secureStorage.getString(API_KEY_KEY)
    }

    override suspend fun hasApiKey(): Boolean {
        return secureStorage.getString(API_KEY_KEY)?.isNotBlank() == true
    }

    override suspend fun clearApiKey() {
        secureStorage.remove(API_KEY_KEY)
        refreshSettings()
    }

    override suspend fun saveImageQuality(quality: ImageQuality) {
        settings.putString(IMAGE_QUALITY_KEY, quality.name)
        refreshSettings()
    }

    override suspend fun saveDarkMode(enabled: Boolean?) {
        if (enabled == null) {
            settings.remove(DARK_MODE_KEY)
        } else {
            settings.putBoolean(DARK_MODE_KEY, enabled)
        }
        refreshSettings()
    }

    private fun loadSettings(): AppSettings {
        val hasKey = secureStorage.getString(API_KEY_KEY)?.isNotBlank() == true
        val qualityName = settings.getStringOrNull(IMAGE_QUALITY_KEY)
        val quality = qualityName?.let {
            try { ImageQuality.valueOf(it) } catch (_: Exception) { null }
        } ?: ImageQuality.MEDIUM
        val darkMode = if (settings.hasKey(DARK_MODE_KEY)) {
            settings.getBoolean(DARK_MODE_KEY, false)
        } else {
            null
        }

        return AppSettings(
            hasApiKey = hasKey,
            imageQuality = quality,
            isDarkMode = darkMode,
        )
    }

    private fun refreshSettings() {
        _settingsFlow.value = loadSettings()
    }

    companion object {
        private const val API_KEY_KEY = "anthropic_api_key"
        private const val IMAGE_QUALITY_KEY = "image_quality"
        private const val DARK_MODE_KEY = "dark_mode"
    }
}
