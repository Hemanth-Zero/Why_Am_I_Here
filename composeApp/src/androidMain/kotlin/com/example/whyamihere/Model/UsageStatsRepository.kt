package com.example.whyamihere.Model

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Calendar
import java.util.TimeZone

class UsageStatsRepository(private val context: Context) {


    fun istMidnightOf(dayOffset: Int = 0): Long {
        val ist = TimeZone.getTimeZone("Asia/Kolkata")
        val cal = Calendar.getInstance(ist)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        return cal.timeInMillis
    }


    fun istEndOf(dayOffset: Int = 0): Long = istMidnightOf(dayOffset) + 24 * 60 * 60 * 1000L - 1

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getTodayUsage(): List<AppUsage> {
        val start = istMidnightOf(0)
        val end   = System.currentTimeMillis().coerceAtMost(istEndOf(0))
        return getUsageForRange(start, end)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun getUsageForDay(dayOffset: Int): List<AppUsage> {
        val start = istMidnightOf(dayOffset)
        val end   = istEndOf(dayOffset).coerceAtMost(System.currentTimeMillis())
        if (end <= start) return emptyList()
        return getUsageForRange(start, end)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getWeeklyUsage(): Map<String, Long> {
        val list = getUsageForRange(istMidnightOf(-6), System.currentTimeMillis())
        return list.associate { it.appName to it.timeUsed }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun getTodayUsageForPackage(packageName: String): Long {
        return getTodayUsage().firstOrNull { it.packageName == packageName }?.timeUsed ?: 0L
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getUsageForRange(startTime: Long, endTime: Long): List<AppUsage> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = usm.queryEvents(startTime, endTime)
        val foregroundStart = mutableMapOf<String, Long>()
        val totalTime       = mutableMapOf<String, Long>()
        val event           = UsageEvents.Event()
        val launchers       = getLauncherPackages(context)

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.packageName in launchers) continue
            val key = "${event.packageName}/${event.className ?: ""}"
            val ts  = event.timeStamp
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if (!foregroundStart.containsKey(key)) foregroundStart[key] = ts
                }
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val s = foregroundStart.remove(key)
                    if (s != null) {
                        val dur = ts - s
                        if (dur in 1..(86_400_000L)) {
                            val pkg = key.substringBefore("/")
                            totalTime[pkg] = (totalTime[pkg] ?: 0L) + dur
                        }
                    }
                }
            }
        }
        // still-running sessions
        foregroundStart.forEach { (key, s) ->
            val pkg = key.substringBefore("/")
            val dur = endTime - s
            if (dur in 1..(86_400_000L))
                totalTime[pkg] = (totalTime[pkg] ?: 0L) + dur
        }

        val pm = context.packageManager
        return totalTime.mapNotNull { (pkg, time) ->
            if (time <= 0) return@mapNotNull null
            try {
                val info = pm.getApplicationInfo(pkg, 0)
                AppUsage(pm.getApplicationLabel(info).toString(), pkg, time)
            } catch (_: Exception) { null }
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
