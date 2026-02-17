package io.inksight.core.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

actual class ImageProcessor {
    actual suspend fun processForUpload(
        imageBytes: ByteArray,
        maxEdge: Int,
        jpegQuality: Int,
    ): ProcessedImage = withContext(Dispatchers.Default) {
        val original = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IllegalArgumentException("Failed to decode image")

        val longestEdge = max(original.width, original.height)
        val bitmap = if (longestEdge > maxEdge) {
            val scale = maxEdge.toFloat() / longestEdge
            val newWidth = (original.width * scale).toInt()
            val newHeight = (original.height * scale).toInt()
            Bitmap.createScaledBitmap(original, newWidth, newHeight, true).also {
                if (it !== original) original.recycle()
            }
        } else {
            original
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, outputStream)
        val resultBytes = outputStream.toByteArray()

        val result = ProcessedImage(
            bytes = resultBytes,
            width = bitmap.width,
            height = bitmap.height,
        )
        if (bitmap !== original) bitmap.recycle()
        result
    }
}
