package io.inksight.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.CoreFoundation.CFDictionaryRef
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.CPointer
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar

@OptIn(ExperimentalForeignApi::class)
actual class SecureStorage {
    private val serviceName = "io.inksight.app"

    actual fun saveString(key: String, value: String) {
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return

        // Delete existing item first, then add new one
        remove(key)

        val query = mutableMapOf<Any?, Any?>()
        query[kSecClass] = kSecClassGenericPassword
        query[kSecAttrService] = serviceName
        query[kSecAttrAccount] = key
        query[kSecValueData] = data

        @Suppress("UNCHECKED_CAST")
        SecItemAdd(query as CFDictionaryRef, null)
    }

    actual fun getString(key: String): String? {
        val query = mutableMapOf<Any?, Any?>()
        query[kSecClass] = kSecClassGenericPassword
        query[kSecAttrService] = serviceName
        query[kSecAttrAccount] = key
        query[kSecReturnData] = true
        query[kSecMatchLimit] = kSecMatchLimitOne

        memScoped {
            val result = alloc<CFTypeRefVar>()
            @Suppress("UNCHECKED_CAST")
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)

            if (status == errSecSuccess) {
                val data = result.value as? NSData ?: return null
                return NSString.create(data = data, encoding = NSUTF8StringEncoding) as? String
            }
        }
        return null
    }

    actual fun remove(key: String) {
        val query = mutableMapOf<Any?, Any?>()
        query[kSecClass] = kSecClassGenericPassword
        query[kSecAttrService] = serviceName
        query[kSecAttrAccount] = key

        @Suppress("UNCHECKED_CAST")
        SecItemDelete(query as CFDictionaryRef)
    }
}
