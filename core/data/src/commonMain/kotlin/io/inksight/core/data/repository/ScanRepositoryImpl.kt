package io.inksight.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import io.inksight.core.data.db.InkSightDatabase
import io.inksight.core.data.db.ScanEntity
import io.inksight.core.domain.model.Scan
import io.inksight.core.domain.model.ScanStatus
import io.inksight.core.domain.repository.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class ScanRepositoryImpl(
    private val database: InkSightDatabase,
) : ScanRepository {

    private val queries get() = database.scanQueries

    override fun getAllScans(): Flow<List<Scan>> =
        queries.getAllScans()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getScanById(id: String): Flow<Scan?> =
        queries.getScanById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }

    override suspend fun searchScans(query: String): List<Scan> = withContext(Dispatchers.Default) {
        queries.searchScans(query)
            .executeAsList()
            .map { it.toDomain() }
    }

    override suspend fun insertScan(scan: Scan): Unit = withContext(Dispatchers.Default) {
        queries.insertScan(
            id = scan.id,
            imagePath = scan.imagePath,
            transcribedText = scan.transcribedText,
            createdAt = scan.createdAt.toEpochMilliseconds(),
            updatedAt = scan.updatedAt.toEpochMilliseconds(),
            status = scan.status.name,
            imageWidth = scan.imageWidth?.toLong(),
            imageHeight = scan.imageHeight?.toLong(),
        )
    }

    override suspend fun updateScan(scan: Scan): Unit = withContext(Dispatchers.Default) {
        queries.updateScan(
            transcribedText = scan.transcribedText,
            updatedAt = scan.updatedAt.toEpochMilliseconds(),
            status = scan.status.name,
            id = scan.id,
        )
    }

    override suspend fun deleteScan(id: String): Unit = withContext(Dispatchers.Default) {
        queries.deleteScan(id)
    }
}

private fun ScanEntity.toDomain() = Scan(
    id = id,
    imagePath = imagePath,
    transcribedText = transcribedText,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
    status = try { ScanStatus.valueOf(status) } catch (_: Exception) { ScanStatus.PENDING },
    imageWidth = imageWidth?.toInt(),
    imageHeight = imageHeight?.toInt(),
)
