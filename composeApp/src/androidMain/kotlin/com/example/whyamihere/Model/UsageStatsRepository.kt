package com.example.whyamihere.Model

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId

class UsageStatsRepository(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getTodayUsage(): List<AppUsage> {

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val startTime = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endTime = System.currentTimeMillis()

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val foregroundStartTimes = mutableMapOf<String, Long>()
        val totalTimeMap = mutableMapOf<String, Long>()
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if(event.packageName in getLauncherPackages(context) ){
                continue
            }
            val pkg = event.packageName
            val activityKey = "$pkg/${event.className ?: ""}"
            val ts = event.timeStamp

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    // Only set start if not already tracking this activity
                    // (avoids double-counting if RESUMED fires twice)
                    if (!foregroundStartTimes.containsKey(activityKey)) {
                        foregroundStartTimes[activityKey] = ts
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val sessionStart = foregroundStartTimes.remove(activityKey)
                    if (sessionStart != null) {
                        val duration = ts - sessionStart
                        // Sanity check — ignore negative or suspiciously huge durations
                        if (duration in 1..(1000 * 60 * 60 * 24)) {
                            totalTimeMap[pkg] = (totalTimeMap[pkg] ?: 0L) + duration
                        }
                    }
                }
            }
        }

        // Handle activities still in foreground right now
        foregroundStartTimes.forEach { (activityKey, sessionStart) ->
            val pkg = activityKey.substringBefore("/")
            val duration = endTime - sessionStart
            if (duration in 1..(1000 * 60 * 60 * 24)) {
                totalTimeMap[pkg] = (totalTimeMap[pkg] ?: 0L) + duration
            }
        }

        val pm = context.packageManager
        val result = mutableListOf<AppUsage>()

        totalTimeMap.forEach { (packageName, timeUsed) ->
            if (timeUsed > 0) {
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    result.add(
                        AppUsage(
                            appName = appName,
                            packageName = packageName,
                            timeUsed = timeUsed
                        )
                    )
                } catch (e: Exception) {
                    // ignore unresolvable system packages
                }
            }
        }

        return result.sortedByDescending { it.timeUsed }
    }

    fun getLauncherPackages(context: Context): Set<String> {

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        val pm = context.packageManager
        val resolveInfo = pm.queryIntentActivities(intent, 0)

        return resolveInfo.map { it.activityInfo.packageName }.toSet()
    }
}

data class AppUsage(
    val appName: String,
    val packageName: String = "",
    val timeUsed: Long
)