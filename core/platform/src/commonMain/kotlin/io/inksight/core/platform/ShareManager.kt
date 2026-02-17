package io.inksight.core.platform

expect class ShareManager {
    fun shareText(text: String, title: String = "")
}
