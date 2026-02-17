package io.inksight.core.domain.usecase

import io.inksight.core.domain.model.Scan
import io.inksight.core.domain.repository.ScanRepository
import kotlinx.coroutines.flow.Flow

class GetScanHistoryUseCase(
    private val scanRepository: ScanRepository,
) {
    operator fun invoke(): Flow<List<Scan>> = scanRepository.getAllScans()

    suspend fun search(query: String): List<Scan> = scanRepository.searchScans(query)
}
