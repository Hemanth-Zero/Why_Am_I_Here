package com.example.whyamihere.ViewModel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.Model.TrackedAppsPrefs
import com.example.whyamihere.Model.UsageStatsRepository

class MyAppViewModel(private val context: Context) : ViewModel() {

    private val usageRepository = UsageStatsRepository(context)

    /* Expose context so screens can create their own repo instances */
    fun getContext(): Context = context

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getUsageList(): List<AppUsage> = usageRepository.getTodayUsage()

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getWeeklyUsage(): Map<String, Long> = usageRepository.getWeeklyUsage()

    fun getNonSystemApps(): List<AppData> {
        val pm              = context.packageManager
        val trackedPackages = TrackedAppsPrefs.getTrackedPackages(context)
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map { app ->
                AppData(
                    name        = pm.getApplicationLabel(app).toString(),
                    icon        = pm.getApplicationIcon(app),
                    packageName = app.packageName,
                    onTrack     = trackedPackages.contains(app.packageName)
                )
            }
            .sortedBy { it.name }
    }

    fun saveTrackedApps(packages: Set<String>) =
        TrackedAppsPrefs.setTrackedPackages(context, packages)

    fun getBreakReminderMinutes(): Int = TrackedAppsPrefs.getBreakReminderMinutes(context)
    fun setBreakReminderMinutes(minutes: Int) =
        TrackedAppsPrefs.setBreakReminderMinutes(context, minutes)

    fun getDailyLimitMinutes(packageName: String): Int =
        TrackedAppsPrefs.getDailyLimitMinutes(context, packageName)
    fun setDailyLimitMinutes(packageName: String, minutes: Int) =
        TrackedAppsPrefs.setDailyLimitMinutes(context, packageName, minutes)
}

data class AppData(
    val name        : String,
    val icon        : Drawable,
    val packageName : String,
    val onTrack     : Boolean
)
