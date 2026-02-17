package io.inksight.core.domain.model

data class AppSettings(
    val hasApiKey: Boolean = false,
    val imageQuality: ImageQuality = ImageQuality.MEDIUM,
    val isDarkMode: Boolean? = null, // null = follow system
    val maxImageLongestEdge: Int = 1568,
)

enum class ImageQuality(val jpegQuality: Int) {
    LOW(60),
    MEDIUM(85),
    HIGH(95),
}
