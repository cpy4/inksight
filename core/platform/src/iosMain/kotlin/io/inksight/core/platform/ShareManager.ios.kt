package io.inksight.core.platform

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager {
    actual fun shareText(text: String, title: String) {
        val items = listOf(text)
        val activityVC = UIActivityViewController(
            activityItems = items,
            applicationActivities = null,
        )

        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(activityVC, animated = true, completion = null)
    }
}
