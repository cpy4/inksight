package io.inksight.core.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Scan(
    val id: String,
    val imagePath: String,
    val transcribedText: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: ScanStatus,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
)

@Serializable
enum class ScanStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
}
