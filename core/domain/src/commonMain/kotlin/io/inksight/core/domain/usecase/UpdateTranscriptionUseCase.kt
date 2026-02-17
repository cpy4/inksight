package io.inksight.core.domain.usecase

import io.inksight.core.domain.model.ScanStatus
import io.inksight.core.domain.repository.ScanRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class UpdateTranscriptionUseCase(
    private val scanRepository: ScanRepository,
) {
    suspend operator fun invoke(scanId: String, newText: String) {
        val scan = scanRepository.getScanById(scanId).first() ?: return
        scanRepository.updateScan(
            scan.copy(
                transcribedText = newText,
                status = ScanStatus.COMPLETED,
                updatedAt = Clock.System.now(),
            )
        )
    }
}
