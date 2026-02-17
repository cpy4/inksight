package io.inksight.core.domain.usecase

import io.inksight.core.domain.model.Scan
import io.inksight.core.domain.model.ScanStatus
import io.inksight.core.domain.model.TranscriptionResult
import io.inksight.core.domain.repository.ScanRepository
import io.inksight.core.domain.repository.TranscriptionRepository
import kotlinx.datetime.Clock

class TranscribeImageUseCase(
    private val transcriptionRepository: TranscriptionRepository,
    private val scanRepository: ScanRepository,
) {
    suspend operator fun invoke(
        scanId: String,
        imageBytes: ByteArray,
        mimeType: String,
        imagePath: String,
    ): TranscriptionResult {
        val now = Clock.System.now()
        val scan = Scan(
            id = scanId,
            imagePath = imagePath,
            transcribedText = "",
            createdAt = now,
            updatedAt = now,
            status = ScanStatus.PROCESSING,
        )
        scanRepository.insertScan(scan)

        val result = transcriptionRepository.transcribeImage(imageBytes, mimeType)

        when (result) {
            is TranscriptionResult.Success -> {
                scanRepository.updateScan(
                    scan.copy(
                        transcribedText = result.text,
                        status = ScanStatus.COMPLETED,
                        updatedAt = Clock.System.now(),
                    )
                )
            }
            is TranscriptionResult.Error -> {
                scanRepository.updateScan(
                    scan.copy(
                        status = ScanStatus.FAILED,
                        updatedAt = Clock.System.now(),
                    )
                )
            }
        }

        return result
    }
}
