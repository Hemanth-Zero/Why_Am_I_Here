package com.example.whyamihere.Model

import android.content.Context
import android.content.SharedPreferences

object TrackedAppsPrefs {
    private const val PREFS_NAME = "tracked_apps_prefs"
    private const val KEY_TRACKED = "tracked_packages"
    private const val KEY_DAILY_LIMIT_PREFIX = "daily_limit_"
    private const val KEY_BREAK_REMINDER_MINS = "break_reminder_mins"

    fun getTrackedPackages(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_TRACKED, emptySet()) ?: emptySet()
    }

    fun setTrackedPackages(context: Context, packages: Set<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_TRACKED, packages).apply()
    }

    fun isTracked(context: Context, packageName: String): Boolean {
        return getTrackedPackages(context).contains(packageName)
    }

    fun getDailyLimitMinutes(context: Context, packageName: String): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DAILY_LIMIT_PREFIX + packageName, 60)
    }

    fun setDailyLimitMinutes(context: Context, packageName: String, minutes: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_DAILY_LIMIT_PREFIX + packageName, minutes).apply()
    }

    fun getBreakReminderMinutes(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_BREAK_REMINDER_MINS, 30)
    }

    fun setBreakReminderMinutes(context: Context, minutes: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_BREAK_REMINDER_MINS, minutes).apply()
    }
}
