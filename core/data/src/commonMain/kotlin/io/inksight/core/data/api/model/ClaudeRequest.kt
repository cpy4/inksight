package io.inksight.core.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val messages: List<Message>,
)

@Serializable
data class Message(
    val role: String,
    val content: List<ContentBlock>,
)

@Serializable
sealed class ContentBlock {
    @Serializable
    @SerialName("image")
    data class Image(
        val type: String = "image",
        val source: ImageSource,
    ) : ContentBlock()

    @Serializable
    @SerialName("text")
    data class Text(
        val type: String = "text",
        val text: String,
    ) : ContentBlock()
}

@Serializable
data class ImageSource(
    val type: String = "base64",
    @SerialName("media_type") val mediaType: String,
    val data: String,
)
