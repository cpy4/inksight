package io.inksight.core.platform

expect class SecureStorage {
    fun saveString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)
}
