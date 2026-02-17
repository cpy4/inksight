package io.inksight.core.domain.repository

import io.inksight.core.domain.model.AppSettings
import io.inksight.core.domain.model.ImageQuality
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun saveApiKey(key: String)
    suspend fun getApiKey(): String?
    suspend fun hasApiKey(): Boolean
    suspend fun clearApiKey()
    suspend fun saveImageQuality(quality: ImageQuality)
    suspend fun saveDarkMode(enabled: Boolean?)
}
