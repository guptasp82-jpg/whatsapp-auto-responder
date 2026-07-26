package com.example.whatsappautoresponder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.TelephonyManager
import com.example.whatsappautoresponder.data.AppDatabase
import com.example.whatsappautoresponder.data.CallRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (!incomingNumber.isNullOrEmpty() && state == TelephonyManager.EXTRA_STATE_IDLE) {
                val db = AppDatabase.getDatabase(context)
                val scope = CoroutineScope(Dispatchers.IO)

                scope.launch {
                    val currentTime = System.currentTimeMillis()
                    val sevenDaysThreshold = currentTime - (7 * 24 * 60 * 60 * 1000L)
                    
                    val repeatCalls = db.callRecordDao().getCallsInLast7Days(incomingNumber, sevenDaysThreshold)
                    val prefs = context.getSharedPreferences("templates", Context.MODE_PRIVATE)

                    val message = if (repeatCalls > 0) {
                        prefs.getString("template_7days", "Welcome back! Thanks for reaching out again.")
                    } else {
                        prefs.getString("template_missed", "Sorry I missed your call. How can I help?")
                    }

                    db.callRecordDao().insertCall(CallRecord(phoneNumber = incomingNumber, timestamp = currentTime, callType = "MISSED"))

                    message?.let {
                        launchWhatsApp(context, incomingNumber, it)
                    }
                }
            }
        }
    }

    private fun launchWhatsApp(context: Context, number: String, message: String) {
        val cleanNumber = number.replace("+", "").replace(" ", "")
        val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
