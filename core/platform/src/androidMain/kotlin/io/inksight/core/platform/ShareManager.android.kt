package io.inksight.core.platform

import android.content.Context
import android.content.Intent

actual class ShareManager(private val context: Context) {
    actual fun shareText(text: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            if (title.isNotEmpty()) {
                putExtra(Intent.EXTRA_SUBJECT, title)
            }
        }
        val chooser = Intent.createChooser(intent, "Share transcription").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
