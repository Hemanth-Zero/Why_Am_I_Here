package com.example.whyamihere.Model

import android.content.Context

object IntentionStore {
    private const val PREFS_NAME = "intention_prefs"
    private const val KEY_INTENTION_PREFIX = "intention_"
    private const val KEY_SESSION_START_PREFIX = "session_start_"
    private const val KEY_LAST_BREAK_PREFIX = "last_break_"

    fun setIntention(context: Context, packageName: String, intention: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_INTENTION_PREFIX + packageName, intention)
            .putLong(KEY_SESSION_START_PREFIX + packageName, System.currentTimeMillis())
            .apply()
    }

    fun getIntention(context: Context, packageName: String): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_INTENTION_PREFIX + packageName, null)
    }

    fun getSessionStartTime(context: Context, packageName: String): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_SESSION_START_PREFIX + packageName, 0L)
    }

    fun clearIntention(context: Context, packageName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_INTENTION_PREFIX + packageName)
            .remove(KEY_SESSION_START_PREFIX + packageName)
            .apply()
    }

    fun updateLastBreakTime(context: Context, packageName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_BREAK_PREFIX + packageName, System.currentTimeMillis()).apply()
    }

    fun getLastBreakTime(context: Context, packageName: String): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_BREAK_PREFIX + packageName, 0L)
    }

    fun hasRecentIntention(context: Context, packageName: String): Boolean {
        val intention = getIntention(context, packageName) ?: return false
        val sessionStart = getSessionStartTime(context, packageName)
        // Intention valid for 5 minutes
        return intention.isNotEmpty() && (System.currentTimeMillis() - sessionStart) < 5 * 60 * 1000
    }
}
