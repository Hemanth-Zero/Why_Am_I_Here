package com.example.whyamihere

import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.whyamihere.Model.IntentionStore
import com.example.whyamihere.Model.TrackedAppsPrefs
import com.example.whyamihere.Model.UsageManager
import com.example.whyamihere.ViewModel.sendBreakNotification

class AppMonitorService : AccessibilityService() {

    private var currentTrackedPackage: String? = null
    private var sessionStartTime: Long = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        if (packageName == "com.android.systemui" ||
            packageName == applicationContext.packageName) return

        val isTracked = TrackedAppsPrefs.isTracked(applicationContext, packageName)

        if (isTracked) {

            val appName = try {
                val pm = applicationContext.packageManager
                pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                packageName
            }

            val usedTime = UsageManager.getTodayUsage(applicationContext, packageName)
            val limitMinutes = TrackedAppsPrefs.getDailyLimitMinutes(applicationContext, packageName)
            val limitMillis = limitMinutes * 60 * 1000L

            val hasIntention = IntentionStore.hasRecentIntention(applicationContext, packageName)

            // 🔴 LIMIT EXCEEDED
            if (limitMillis > 0 && usedTime >= limitMillis) {
                OverlayService.show(applicationContext, packageName, appName)

                currentTrackedPackage = packageName
                sessionStartTime = System.currentTimeMillis()
                return
            }

            // 🟡 NO INTENTION
            if (!hasIntention) {
                OverlayService.show(applicationContext, packageName, appName)

                currentTrackedPackage = packageName
                sessionStartTime = System.currentTimeMillis()
            } else {
                checkBreakReminder(packageName)
            }

        } else {
            currentTrackedPackage?.let { prevPkg ->
                if (prevPkg != packageName) {

                    val duration = System.currentTimeMillis() - sessionStartTime

                    if (duration > 0) {
                        UsageManager.saveUsage(applicationContext, prevPkg, duration)
                    }

                    IntentionStore.clearIntention(applicationContext, prevPkg)
                    currentTrackedPackage = null
                }
            }
        }
    }

    private fun checkBreakReminder(packageName: String) {
        val breakIntervalMs =
            TrackedAppsPrefs.getBreakReminderMinutes(applicationContext) * 60 * 1000L

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
        }
    }

    override fun onInterrupt() {}
}