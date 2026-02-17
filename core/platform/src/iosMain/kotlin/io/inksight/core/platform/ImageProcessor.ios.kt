package io.inksight.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy
import kotlin.math.max

@OptIn(ExperimentalForeignApi::class)
actual class ImageProcessor {
    actual suspend fun processForUpload(
        imageBytes: ByteArray,
        maxEdge: Int,
        jpegQuality: Int,
    ): ProcessedImage = withContext(Dispatchers.IO) {
        val nsData = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        val uiImage = UIImage(data = nsData)
            ?: throw IllegalArgumentException("Failed to decode image")

        val originalWidth = uiImage.size.useContents { width }.toInt()
        val originalHeight = uiImage.size.useContents { height }.toInt()
        val longestEdge = max(originalWidth, originalHeight)

        val targetImage = if (longestEdge > maxEdge) {
            val scale = maxEdge.toFloat() / longestEdge
            val newWidth = (originalWidth * scale).toInt()
            val newHeight = (originalHeight * scale).toInt()
            resizeImage(uiImage, newWidth, newHeight) ?: uiImage
        } else {
            uiImage
        }

        val quality = jpegQuality / 100.0
        val jpegData = UIImageJPEGRepresentation(targetImage, quality)
            ?: throw IllegalStateException("Failed to compress image to JPEG")

        val resultBytes = jpegData.toByteArray()

        val finalWidth = targetImage.size.useContents { width }.toInt()
        val finalHeight = targetImage.size.useContents { height }.toInt()

        ProcessedImage(
            bytes = resultBytes,
            width = finalWidth,
            height = finalHeight,
        )
    }

    private fun resizeImage(image: UIImage, targetWidth: Int, targetHeight: Int): UIImage? {
        val size = CGSizeMake(targetWidth.toDouble(), targetHeight.toDouble())
        UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
        image.drawInRect(CGRectMake(0.0, 0.0, targetWidth.toDouble(), targetHeight.toDouble()))
        val resized = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return resized
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        bytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return bytes
}
