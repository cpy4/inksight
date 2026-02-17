package io.inksight.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
actual class FileManager {
    private val fileManager = NSFileManager.defaultManager

    private val imagesDir: String
        get() {
            val docs = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).first() as String
            val dir = "$docs/scan_images"
            if (!fileManager.fileExistsAtPath(dir)) {
                fileManager.createDirectoryAtPath(dir, true, null, null)
            }
            return dir
        }

    actual suspend fun saveImage(bytes: ByteArray, fileName: String): String =
        withContext(Dispatchers.IO) {
            val path = "$imagesDir/$fileName"
            val data = bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
            }
            data.writeToFile(path, atomically = true)
            path
        }

    actual suspend fun readImage(path: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val data = NSData.create(contentsOfFile = path) ?: return@withContext null
            data.toByteArray()
        }

    actual suspend fun deleteImage(path: String): Boolean =
        withContext(Dispatchers.IO) {
            fileManager.removeItemAtPath(path, null)
        }

    actual fun getImagesDirectory(): String = imagesDir
}
