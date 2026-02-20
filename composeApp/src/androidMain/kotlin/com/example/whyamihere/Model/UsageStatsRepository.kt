package com.example.whyamihere.Model

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

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

        val statsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val pm = context.packageManager
        val result = mutableListOf<AppUsage>()

        statsList?.forEach { usageStats ->

            val timeUsed = usageStats.totalTimeInForeground

            if (timeUsed > 0) {
                try {
                    val appInfo = pm.getApplicationInfo(usageStats.packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo).toString()

                    result.add(
                        AppUsage(
                            appName = appName,
                            timeUsed = timeUsed
                        )
                    )
                } catch (e: Exception) {
                    // ignore system apps not resolvable
                }
            }
        }

        return result.sortedByDescending { it.timeUsed }
    }



}
