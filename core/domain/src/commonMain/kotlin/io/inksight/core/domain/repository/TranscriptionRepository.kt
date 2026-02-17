package io.inksight.core.domain.repository

import io.inksight.core.domain.model.TranscriptionResult

interface TranscriptionRepository {
    suspend fun transcribeImage(
        imageBytes: ByteArray,
        mimeType: String,
    ): TranscriptionResult
}
