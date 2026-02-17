package io.inksight.core.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ResponseContent>,
    val model: String,
    @SerialName("stop_reason") val stopReason: String? = null,
    val usage: Usage? = null,
)

@Serializable
data class ResponseContent(
    val type: String,
    val text: String? = null,
)

@Serializable
data class Usage(
    @SerialName("input_tokens") val inputTokens: Int,
    @SerialName("output_tokens") val outputTokens: Int,
)

@Serializable
data class ClaudeErrorResponse(
    val type: String,
    val error: ClaudeError,
)

@Serializable
data class ClaudeError(
    val type: String,
    val message: String,
)
