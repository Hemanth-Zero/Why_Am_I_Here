package com.example.whyamihere.Model

import android.content.Context

object TrackedAppsPrefs {
    private const val PREFS_NAME              = "tracked_apps_prefs"
    private const val KEY_TRACKED             = "tracked_packages"
    private const val KEY_DAILY_LIMIT_PREFIX  = "daily_limit_"
    private const val KEY_BREAK_REMINDER_MINS = "break_reminder_mins"
    private const val KEY_UNLIMITED_PREFIX    = "unlimited_"

    // Profile fields
    private const val KEY_PROFILE_NAME     = "profile_name"
    private const val KEY_PROFILE_EMAIL    = "profile_email"
    private const val KEY_PROFILE_AGE      = "profile_age"
    private const val KEY_PROFILE_LOCATION = "profile_location"
    private const val KEY_PROFILE_PHONE    = "profile_phone"

    // Theme & preferences
    private const val KEY_THEME_MODE       = "theme_mode"    // "dark" | "light" | "system"
    private const val KEY_DYNAMIC_COLOR    = "dynamic_color"
    private const val KEY_FONT_SCALE       = "font_scale"

    // ── Tracked apps ──────────────────────────────────────────────────────────

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

    fun isUnlimited(context: Context, packageName: String): Boolean =
        prefs(context).getBoolean(KEY_UNLIMITED_PREFIX + packageName, false)

    fun setUnlimited(context: Context, packageName: String, value: Boolean) =
        prefs(context).edit().putBoolean(KEY_UNLIMITED_PREFIX + packageName, value).apply()

    fun clearAllUnlimitedFlags(context: Context) {
        val editor = prefs(context).edit()
        prefs(context).all.keys
            .filter { it.startsWith(KEY_UNLIMITED_PREFIX) }
            .forEach { editor.remove(it) }
        editor.apply()
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    fun getProfileName(context: Context): String =
        prefs(context).getString(KEY_PROFILE_NAME, "Hemanth") ?: "Hemanth"

    fun setProfileName(context: Context, value: String) =
        prefs(context).edit().putString(KEY_PROFILE_NAME, value).apply()

    fun getProfileEmail(context: Context): String =
        prefs(context).getString(KEY_PROFILE_EMAIL, "hemanth@123.gmail.com") ?: ""

    fun setProfileEmail(context: Context, value: String) =
        prefs(context).edit().putString(KEY_PROFILE_EMAIL, value).apply()

    fun getProfileAge(context: Context): Int =
        prefs(context).getInt(KEY_PROFILE_AGE, 20)

    fun setProfileAge(context: Context, value: Int) =
        prefs(context).edit().putInt(KEY_PROFILE_AGE, value).apply()

    fun getProfileLocation(context: Context): String =
        prefs(context).getString(KEY_PROFILE_LOCATION, "Bangalore") ?: ""

    fun setProfileLocation(context: Context, value: String) =
        prefs(context).edit().putString(KEY_PROFILE_LOCATION, value).apply()

    fun getProfilePhone(context: Context): String =
        prefs(context).getString(KEY_PROFILE_PHONE, "+91 9876543210") ?: ""

    fun setProfilePhone(context: Context, value: String) =
        prefs(context).edit().putString(KEY_PROFILE_PHONE, value).apply()

    // ── Theme & Preferences ───────────────────────────────────────────────────

    /** Returns "dark", "light", or "system" */
    fun getThemeMode(context: Context): String =
        prefs(context).getString(KEY_THEME_MODE, "dark") ?: "dark"

    fun setThemeMode(context: Context, mode: String) =
        prefs(context).edit().putString(KEY_THEME_MODE, mode).apply()

    fun getDynamicColor(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DYNAMIC_COLOR, false)

    fun setDynamicColor(context: Context, value: Boolean) =
        prefs(context).edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()

    fun getFontScale(context: Context): Float =
        prefs(context).getFloat(KEY_FONT_SCALE, 1.0f)

    fun setFontScale(context: Context, scale: Float) =
        prefs(context).edit().putFloat(KEY_FONT_SCALE, scale).apply()

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
