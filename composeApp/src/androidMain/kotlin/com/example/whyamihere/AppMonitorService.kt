package com.example.whyamihere

import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.whyamihere.Model.IntentionStore
import com.example.whyamihere.Model.TrackedAppsPrefs
import com.example.whyamihere.ViewModel.sendBreakNotification
import com.example.whyamihere.ViewModel.sendNotification

class AppMonitorService : AccessibilityService() {

    private var currentTrackedPackage: String? = null
    private var sessionStartTime: Long = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        // Skip system UI and our own app
        if (packageName == "com.android.systemui" ||
            packageName == applicationContext.packageName) return

        val isTracked = TrackedAppsPrefs.isTracked(applicationContext, packageName)

        if (isTracked) {
            val hasIntention = IntentionStore.hasRecentIntention(applicationContext, packageName)

            if (!hasIntention) {
                // Show overlay to prompt intention
                val appName = try {
                    val pm = applicationContext.packageManager
                    pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    packageName
                }

                currentTrackedPackage = packageName
                sessionStartTime = System.currentTimeMillis()

                OverlayService.show(applicationContext, packageName, appName)
                Log.d("AppMonitor", "Showing intention overlay for $packageName")
            } else {
                // App opened with intention — check break reminder
                checkBreakReminder(packageName)
            }
        } else {
            // Left a tracked app — clear its intention
            currentTrackedPackage?.let { prevPkg ->
                if (prevPkg != packageName) {
                    IntentionStore.clearIntention(applicationContext, prevPkg)
                    currentTrackedPackage = null
                }
            }
        }
    }

    private fun checkBreakReminder(packageName: String) {
        val breakIntervalMs = TrackedAppsPrefs.getBreakReminderMinutes(applicationContext) * 60 * 1000L
        val lastBreak = IntentionStore.getLastBreakTime(applicationContext, packageName)
        val sessionStart = IntentionStore.getSessionStartTime(applicationContext, packageName)
        val reference = if (lastBreak > sessionStart) lastBreak else sessionStart

        if (reference > 0 && (System.currentTimeMillis() - reference) >= breakIntervalMs) {
            IntentionStore.updateLastBreakTime(applicationContext, packageName)
            val appName = try {
                val pm = applicationContext.packageManager
                pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
            } catch (e: Exception) { packageName }
            sendBreakNotification(applicationContext, appName)
            Log.d("AppMonitor", "Break reminder sent for $packageName")
        }
    }

    override fun onInterrupt() {}
}
