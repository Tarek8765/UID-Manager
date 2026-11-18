package com.uidmanager.app.clipboard

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.uidmanager.app.overlay.OverlayBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ClipboardActivity : ComponentActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val target = intent.getStringExtra("target") ?: run { finish(); return }
        val text = readClipboard()

        Log.d("ClipboardActivity", "target=$target text=${text?.take(40)}")

        when (target) {
            "uid" -> {
                val uid = extractUid(text)
                if (uid != null) {
                    OverlayBridge.onUidCaptured(uid)
                } else {
                    OverlayBridge.onError("No 15-17 digit UID found in clipboard")
                }
            }
            "cookies" -> {
                if (isValidCookie(text)) {
                    OverlayBridge.onCookiesCaptured(text!!)
                } else {
                    OverlayBridge.onError("Clipboard does not contain valid cookies (c_user/xs)")
                }
            }
        }
        finish()
    }

    private fun readClipboard(): String? {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).text?.toString()
    }

    private fun extractUid(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val regex = Regex("\\b(\\d{15,17})\\b")
        return regex.find(text)?.groupValues?.get(1)
    }

    private fun isValidCookie(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        val hasCUser = Regex("c_user=([^;\\s]+)").containsMatchIn(text)
        val hasXs = Regex("xs=([^;\\s]+)").containsMatchIn(text)
        return hasCUser && hasXs
    }
}
