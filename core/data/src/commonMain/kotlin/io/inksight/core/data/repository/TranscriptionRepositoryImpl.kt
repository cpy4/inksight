package io.inksight.core.data.repository

import io.inksight.core.data.api.ClaudeApiClient
import io.inksight.core.data.api.model.ClaudeErrorResponse
import io.inksight.core.domain.model.TranscriptionResult
import io.inksight.core.domain.repository.TranscriptionRepository
import io.inksight.core.platform.SecureStorage
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class TranscriptionRepositoryImpl(
    private val claudeApiClient: ClaudeApiClient,
    private val secureStorage: SecureStorage,
) : TranscriptionRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun transcribeImage(
        imageBytes: ByteArray,
        mimeType: String,
    ): TranscriptionResult {
        val apiKey = secureStorage.getString(API_KEY_STORAGE_KEY)
            ?: return TranscriptionResult.Error("API key not configured. Please set your Anthropic API key in Settings.")

        val base64Image = Base64.encode(imageBytes)

        return try {
            val response = claudeApiClient.transcribeImage(
                apiKey = apiKey,
                imageBase64 = base64Image,
                mediaType = mimeType,
            )
            val text = response.content
                .firstOrNull { it.type == "text" }
                ?.text
                ?.trim()
                ?: ""

            if (text.isEmpty()) {
                TranscriptionResult.Error("No text was detected in the image.")
            } else {
                TranscriptionResult.Success(text)
            }
        } catch (e: ClientRequestException) {
            handleClientError(e)
        } catch (e: ServerResponseException) {
            TranscriptionResult.Error(
                message = "Server error (${e.response.status.value}). Please try again.",
                isRetryable = true,
            )
        } catch (e: Exception) {
            TranscriptionResult.Error(
                message = "Network error: ${e.message ?: "Unknown error"}",
                isRetryable = true,
            )
        }
    }

    private suspend fun handleClientError(e: ClientRequestException): TranscriptionResult {
        val statusCode = e.response.status.value
        val body = try { e.response.bodyAsText() } catch (_: Exception) { "" }
        val errorMessage = try {
            json.decodeFromString<ClaudeErrorResponse>(body).error.message
        } catch (_: Exception) {
            null
        }

        return when (statusCode) {
            401 -> TranscriptionResult.Error(
                message = "Invalid API key. Please check your key in Settings.",
                isRetryable = false,
            )
            400 -> TranscriptionResult.Error(
                message = errorMessage ?: "Bad request. The image may be too large or in an unsupported format.",
                isRetryable = false,
            )
            429 -> TranscriptionResult.Error(
                message = "Rate limited. Please wait a moment and try again.",
                isRetryable = true,
            )
            else -> TranscriptionResult.Error(
                message = errorMessage ?: "API error ($statusCode)",
                isRetryable = statusCode >= 500,
            )
        }
    }

    companion object {
        const val API_KEY_STORAGE_KEY = "anthropic_api_key"
    }
}
