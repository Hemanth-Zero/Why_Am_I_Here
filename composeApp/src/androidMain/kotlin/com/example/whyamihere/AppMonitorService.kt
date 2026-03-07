package com.example.whyamihere

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.whyamihere.ViewModel.sendNotification

class AppMonitorService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()

            if (packageName != null && packageName == "com.instagram.android") {
                sendNotification(this, "Opened: $packageName")
            }

        }
    }

    override fun onInterrupt() {}
}