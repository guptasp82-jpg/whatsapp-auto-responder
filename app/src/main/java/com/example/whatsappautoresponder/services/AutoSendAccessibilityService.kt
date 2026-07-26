package com.example.whatsappautoresponder.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoSendAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val rootNode = rootInActiveWindow ?: return
        val packageName = event.packageName?.toString() ?: ""

        if (packageName != "com.whatsapp" && packageName != "com.whatsapp.w4b") return

        val sendButtonNodes = rootNode.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
            .ifEmpty { rootNode.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/send") }

        if (!sendButtonNodes.isNullOrEmpty()) {
            val sendButton = sendButtonNodes[0]
            if (sendButton.isEnabled) {
                sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Thread.sleep(600)
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
        }
    }

    override fun onInterrupt() {}
}
