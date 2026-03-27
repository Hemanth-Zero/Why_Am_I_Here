package com.example.whyamihere

import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.whyamihere.Model.TrackedAppsPrefs
import com.example.whyamihere.Model.UsageStatsRepository
import com.example.whyamihere.ViewModel.sendBreakNotification

class AppMonitorService : AccessibilityService() {

    private val usageRepo by lazy { UsageStatsRepository(applicationContext) }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == "com.android.systemui" ||
            packageName == applicationContext.packageName) return

        val isTracked = TrackedAppsPrefs.isTracked(applicationContext, packageName)
        if (!isTracked) return

        // If a break timer is currently running, never show overlay
        if (BreakTimerService.activeTimers.contains(packageName)) {
            return
        }

        // If unlimited flag is set for today → skip overlay
        if (TrackedAppsPrefs.isUnlimited(applicationContext, packageName)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkAndShowOverlay(packageName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkAndShowOverlay(packageName: String) {
        val limitMs   = TrackedAppsPrefs.getDailyLimitMinutes(applicationContext, packageName) * 60_000L
        val usedToday = usageRepo.getTodayUsageForPackage(packageName)

        if (usedToday >= limitMs) {
            val appName = resolveAppName(packageName)
            Log.d("AppMonitor", "Usage $usedToday >= limit $limitMs → showing overlay for $packageName")
            OverlayService.show(applicationContext, packageName, appName)
        }
    }

    private fun resolveAppName(packageName: String): String = try {
        val pm = applicationContext.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
    } catch (_: PackageManager.NameNotFoundException) { packageName }

    override fun onInterrupt() {}
}
