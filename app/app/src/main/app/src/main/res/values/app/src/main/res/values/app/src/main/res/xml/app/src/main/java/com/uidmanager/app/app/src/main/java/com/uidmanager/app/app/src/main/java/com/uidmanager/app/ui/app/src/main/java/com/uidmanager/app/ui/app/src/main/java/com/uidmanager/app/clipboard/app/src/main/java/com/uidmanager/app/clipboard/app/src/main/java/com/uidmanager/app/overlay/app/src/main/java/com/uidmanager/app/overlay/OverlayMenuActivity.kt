package com.uidmanager.app.overlay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uidmanager.app.clipboard.ClipboardActivity

class OverlayMenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .width(220.dp)
                            .background(Color(0xF0FFFFFF), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Paste from clipboard", style = MaterialTheme.typography.titleMedium)

                        Button(
                            onClick = { launchClipboard("uid") },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("UID") }

                        Button(
                            onClick = { launchClipboard("cookies") },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Cookies") }

                        Button(
                            onClick = { finish() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Close") }
                    }
                }
            }
        }
    }

    private fun launchClipboard(target: String) {
        val i = Intent(this, ClipboardActivity::class.java).apply {
            putExtra("target", target)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(i)
        finish()
    }
}
