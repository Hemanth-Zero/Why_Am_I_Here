package com.example.whyamihere.Model

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class UsageStatsRepository(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getTodayUsage(): List<AppUsage> {
        val startTime = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return getUsageForRange(startTime, System.currentTimeMillis())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getWeeklyUsage(): Map<String, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startTime = cal.timeInMillis
        val list = getUsageForRange(startTime, System.currentTimeMillis())
        return list.associate { it.appName to it.timeUsed }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getUsageForRange(startTime: Long, endTime: Long): List<AppUsage> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val foregroundStartTimes = mutableMapOf<String, Long>()
        val totalTimeMap = mutableMapOf<String, Long>()
        val event = UsageEvents.Event()

        val launchers = getLauncherPackages(context)

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.packageName in launchers) continue

            val pkg = event.packageName
            val activityKey = "$pkg/${event.className ?: ""}"
            val ts = event.timeStamp

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if (!foregroundStartTimes.containsKey(activityKey)) {
                        foregroundStartTimes[activityKey] = ts
                    }
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val sessionStart = foregroundStartTimes.remove(activityKey)
                    if (sessionStart != null) {
                        val duration = ts - sessionStart
                        if (duration in 1..(1000 * 60 * 60 * 24)) {
                            totalTimeMap[pkg] = (totalTimeMap[pkg] ?: 0L) + duration
                        }
                    }
                }
            }
        }

        foregroundStartTimes.forEach { (activityKey, sessionStart) ->
            val pkg = activityKey.substringBefore("/")
            val duration = endTime - sessionStart
            if (duration in 1..(1000 * 60 * 60 * 24)) {
                totalTimeMap[pkg] = (totalTimeMap[pkg] ?: 0L) + duration
            }
        }

        val pm = context.packageManager
        return totalTimeMap.mapNotNull { (packageName, timeUsed) ->
            if (timeUsed <= 0) return@mapNotNull null
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                AppUsage(
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    packageName = packageName,
                    timeUsed = timeUsed
                )
            } catch (e: Exception) { null }
        }.sortedByDescending { it.timeUsed }
    }

    fun getLauncherPackages(context: Context): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        return context.packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }.toSet()
    }
}

data class AppUsage(
    val appName: String,
    val packageName: String = "",
    val timeUsed: Long
)
