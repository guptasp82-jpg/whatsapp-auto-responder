package com.example.whatsappautoresponder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG),
            101
        )

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TemplateManagerScreen(
                        onOpenAccessibilitySettings = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        context = this
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateManagerScreen(onOpenAccessibilitySettings: () -> Unit, context: Context) {
    val prefs = remember { context.getSharedPreferences("templates", Context.MODE_PRIVATE) }

    var missedCallMsg by remember { mutableStateOf(prefs.getString("template_missed", "Sorry I missed your call.") ?: "") }
    var receivedCallMsg by remember { mutableStateOf(prefs.getString("template_received", "Thanks for reaching out!") ?: "") }
    var repeatCallMsg by remember { mutableStateOf(prefs.getString("template_7days", "Welcome back! Thanks for calling again.") ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("WhatsApp Auto-Responder Settings", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onOpenAccessibilitySettings, modifier = Modifier.fillMaxWidth()) {
            Text("Enable Accessibility Auto-Send Permission")
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = missedCallMsg,
            onValueChange = { missedCallMsg = it },
            label = { Text("Template: Missed Call") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = receivedCallMsg,
            onValueChange = { receivedCallMsg = it },
            label = { Text("Template: Received Call") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = repeatCallMsg,
            onValueChange = { repeatCallMsg = it },
            label = { Text("Template: Repeat Call within 7 Days") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                prefs.edit()
                    .putString("template_missed", missedCallMsg)
                    .putString("template_received", receivedCallMsg)
                    .putString("template_7days", repeatCallMsg)
                    .apply()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Templates")
        }
    }
}
