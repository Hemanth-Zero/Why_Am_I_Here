package com.example.whyamihere.ViewModel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.Model.TrackedAppsPrefs
import com.example.whyamihere.Model.UsageStatsRepository

class MyAppViewModel(private val context: Context) : ViewModel() {

    private val usageRepository = UsageStatsRepository(context)

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

    // ── Profile ───────────────────────────────────────────────────────────────

    var profileName     by mutableStateOf(TrackedAppsPrefs.getProfileName(context))
    var profileEmail    by mutableStateOf(TrackedAppsPrefs.getProfileEmail(context))
    var profileAge      by mutableStateOf(TrackedAppsPrefs.getProfileAge(context))
    var profileLocation by mutableStateOf(TrackedAppsPrefs.getProfileLocation(context))
    var profilePhone    by mutableStateOf(TrackedAppsPrefs.getProfilePhone(context))

    fun saveProfile(name: String, email: String, age: Int, location: String, phone: String) {
        profileName     = name;     TrackedAppsPrefs.setProfileName(context, name)
        profileEmail    = email;    TrackedAppsPrefs.setProfileEmail(context, email)
        profileAge      = age;      TrackedAppsPrefs.setProfileAge(context, age)
        profileLocation = location; TrackedAppsPrefs.setProfileLocation(context, location)
        profilePhone    = phone;    TrackedAppsPrefs.setProfilePhone(context, phone)
    }

    // ── Theme ─────────────────────────────────────────────────────────────────

    private var _themeMode    by mutableStateOf(TrackedAppsPrefs.getThemeMode(context))
    val themeMode: String get() = _themeMode

    private var _dynamicColor by mutableStateOf(TrackedAppsPrefs.getDynamicColor(context))
    val dynamicColor: Boolean get() = _dynamicColor

    private var _fontScale    by mutableStateOf(TrackedAppsPrefs.getFontScale(context))
    val fontScale: Float get() = _fontScale

    fun setThemeMode(mode: String) {
        _themeMode = mode
        TrackedAppsPrefs.setThemeMode(context, mode)
    }

    fun setDynamicColor(value: Boolean) {
        _dynamicColor = value
        TrackedAppsPrefs.setDynamicColor(context, value)
    }

    fun setFontScale(scale: Float) {
        _fontScale = scale
        TrackedAppsPrefs.setFontScale(context, scale)
    }
}

data class AppData(
    val name        : String,
    val icon        : Drawable,
    val packageName : String,
    val onTrack     : Boolean
)
