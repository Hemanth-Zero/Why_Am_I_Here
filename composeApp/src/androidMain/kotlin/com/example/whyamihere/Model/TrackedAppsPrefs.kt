package com.example.whyamihere.Model

import android.content.Context

object TrackedAppsPrefs {
    private const val PREFS_NAME              = "tracked_apps_prefs"
    private const val KEY_TRACKED             = "tracked_packages"
    private const val KEY_DAILY_LIMIT_PREFIX  = "daily_limit_"
    private const val KEY_BREAK_REMINDER_MINS = "break_reminder_mins"
    private const val KEY_UNLIMITED_PREFIX    = "unlimited_"

    fun getTrackedPackages(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_TRACKED, emptySet()) ?: emptySet()

    fun setTrackedPackages(context: Context, packages: Set<String>) =
        prefs(context).edit().putStringSet(KEY_TRACKED, packages).apply()

    fun isTracked(context: Context, packageName: String): Boolean =
        getTrackedPackages(context).contains(packageName)

    fun getDailyLimitMinutes(context: Context, packageName: String): Int =
        prefs(context).getInt(KEY_DAILY_LIMIT_PREFIX + packageName, 60)

    fun setDailyLimitMinutes(context: Context, packageName: String, minutes: Int) =
        prefs(context).edit().putInt(KEY_DAILY_LIMIT_PREFIX + packageName, minutes).apply()

    fun getBreakReminderMinutes(context: Context): Int =
        prefs(context).getInt(KEY_BREAK_REMINDER_MINS, 30)

    fun setBreakReminderMinutes(context: Context, minutes: Int) =
        prefs(context).edit().putInt(KEY_BREAK_REMINDER_MINS, minutes).apply()

    /** When true the overlay will NOT retrigger for today for this package */
    fun isUnlimited(context: Context, packageName: String): Boolean =
        prefs(context).getBoolean(KEY_UNLIMITED_PREFIX + packageName, false)

    fun setUnlimited(context: Context, packageName: String, value: Boolean) =
        prefs(context).edit().putBoolean(KEY_UNLIMITED_PREFIX + packageName, value).apply()

    /** Reset unlimited flags at midnight */
    fun clearAllUnlimitedFlags(context: Context) {
        val editor = prefs(context).edit()
        prefs(context).all.keys
            .filter { it.startsWith(KEY_UNLIMITED_PREFIX) }
            .forEach { editor.remove(it) }
        editor.apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
