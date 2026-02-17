package io.inksight.core.platform

expect class FileManager {
    suspend fun saveImage(bytes: ByteArray, fileName: String): String
    suspend fun readImage(path: String): ByteArray?
    suspend fun deleteImage(path: String): Boolean
    fun getImagesDirectory(): String
}
