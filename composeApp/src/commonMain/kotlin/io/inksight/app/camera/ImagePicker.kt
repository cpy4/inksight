package io.inksight.app.camera

import androidx.compose.runtime.Composable

/**
 * Result from picking or capturing an image.
 */
data class ImagePickResult(
    val imageBytes: ByteArray,
    val mimeType: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImagePickResult) return false
        return imageBytes.contentEquals(other.imageBytes) && mimeType == other.mimeType
    }

    override fun hashCode(): Int {
        var result = imageBytes.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

/**
 * Composable that provides image picking capability from the gallery.
 * When [launch] is called from the callback, the platform gallery picker is presented.
 */
@Composable
expect fun rememberGalleryPickerLauncher(
    onResult: (ImagePickResult?) -> Unit,
): () -> Unit

/**
 * Composable that provides camera capture capability.
 * When [launch] is called from the callback, the platform camera is presented.
 */
@Composable
expect fun rememberCameraCaptureLauncher(
    onResult: (ImagePickResult?) -> Unit,
): () -> Unit
