package io.inksight.core.data.api

import io.inksight.core.data.api.model.ClaudeRequest
import io.inksight.core.data.api.model.ClaudeResponse
import io.inksight.core.data.api.model.ContentBlock
import io.inksight.core.data.api.model.ImageSource
import io.inksight.core.data.api.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ClaudeApiClient(
    private val httpClient: HttpClient,
) {
    companion object {
        private const val BASE_URL = "https://api.anthropic.com/v1/messages"
        private const val API_VERSION = "2023-06-01"
        private const val MODEL = "claude-sonnet-4-5-20250929"
        private const val MAX_TOKENS = 4096

        internal const val TRANSCRIPTION_PROMPT = """Transcribe all handwritten text visible in this image accurately. Preserve paragraph breaks and formatting. If any words are unclear, use [unclear] as a placeholder. Output ONLY the transcribed text with no commentary, headers, or markdown formatting."""
    }

    suspend fun transcribeImage(
        apiKey: String,
        imageBase64: String,
        mediaType: String,
        prompt: String = TRANSCRIPTION_PROMPT,
    ): ClaudeResponse {
        val request = ClaudeRequest(
            model = MODEL,
            maxTokens = MAX_TOKENS,
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        ContentBlock.Image(
                            source = ImageSource(
                                mediaType = mediaType,
                                data = imageBase64,
                            )
                        ),
                        ContentBlock.Text(text = prompt),
                    )
                )
            )
        )

        return httpClient.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("x-api-key", apiKey)
            header("anthropic-version", API_VERSION)
            setBody(request)
        }.body()
    }
}
