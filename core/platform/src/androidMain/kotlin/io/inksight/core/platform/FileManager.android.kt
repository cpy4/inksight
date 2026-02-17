package io.inksight.core.platform

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class FileManager(private val context: Context) {
    private val imagesDir: File
        get() = File(context.filesDir, "scan_images").also { it.mkdirs() }

    actual suspend fun saveImage(bytes: ByteArray, fileName: String): String =
        withContext(Dispatchers.IO) {
            val file = File(imagesDir, fileName)
            file.writeBytes(bytes)
            file.absolutePath
        }

    actual suspend fun readImage(path: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val file = File(path)
            if (file.exists()) file.readBytes() else null
        }

    actual suspend fun deleteImage(path: String): Boolean =
        withContext(Dispatchers.IO) {
            File(path).delete()
        }

    actual fun getImagesDirectory(): String = imagesDir.absolutePath
}
