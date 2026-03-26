package com.example.whyamihere.Model

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object UsageManager {

    fun getTodayUsage(context: Context, packageName: String): Long {
        val prefs = context.getSharedPreferences("usage", Context.MODE_PRIVATE)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return prefs.getLong("$packageName-$today", 0L)
    }

    fun saveUsage(context: Context, packageName: String, duration: Long) {
        val prefs = context.getSharedPreferences("usage", Context.MODE_PRIVATE)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val key = "$packageName-$today"
        val existing = prefs.getLong(key, 0L)

        prefs.edit().putLong(key, existing + duration).apply()
    }
}