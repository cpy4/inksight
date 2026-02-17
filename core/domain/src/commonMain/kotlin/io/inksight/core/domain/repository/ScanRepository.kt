package io.inksight.core.domain.repository

import io.inksight.core.domain.model.Scan
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
    fun getAllScans(): Flow<List<Scan>>
    fun getScanById(id: String): Flow<Scan?>
    suspend fun searchScans(query: String): List<Scan>
    suspend fun insertScan(scan: Scan)
    suspend fun updateScan(scan: Scan)
    suspend fun deleteScan(id: String)
}
