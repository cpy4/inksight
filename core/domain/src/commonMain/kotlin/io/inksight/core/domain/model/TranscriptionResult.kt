package io.inksight.core.domain.model

sealed class TranscriptionResult {
    data class Success(val text: String) : TranscriptionResult()
    data class Error(
        val message: String,
        val isRetryable: Boolean = false,
    ) : TranscriptionResult()
}
