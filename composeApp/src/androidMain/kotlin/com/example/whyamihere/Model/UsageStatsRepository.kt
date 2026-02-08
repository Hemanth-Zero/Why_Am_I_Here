package com.example.whyamihere.Model

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class UsageStatsRepository(private val context: Context){
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getLast24HoursUsage():List<AppUsage>{
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000)

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,endTime
        )

        val pm: PackageManager = context.packageManager //Database of Apps
        val result = mutableListOf<AppUsage>()

        for( usage in stats){
            if(usage.totalTimeInForeground>0) {
                try {
                    val appInfo = pm.getApplicationInfo(usage.packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    result.add(
                        AppUsage(
                            appName = appName,
                            packageName = usage.packageName,
                            timeUsed = usage.totalTimeInForeground
                        )
                    )
                } catch (e: Exception) {
                    //Log.d(e.toString(),usage.packageName)
                }
            }
        }
        return result
    }
}