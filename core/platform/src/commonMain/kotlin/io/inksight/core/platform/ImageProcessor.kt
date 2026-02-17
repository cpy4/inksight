package io.inksight.core.platform

data class ProcessedImage(
    val bytes: ByteArray,
    val width: Int,
    val height: Int,
    val mimeType: String = "image/jpeg",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProcessedImage) return false
        return bytes.contentEquals(other.bytes) && width == other.width &&
            height == other.height && mimeType == other.mimeType
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

expect class ImageProcessor {
    suspend fun processForUpload(
        imageBytes: ByteArray,
        maxEdge: Int = 1568,
        jpegQuality: Int = 85,
    ): ProcessedImage
}
