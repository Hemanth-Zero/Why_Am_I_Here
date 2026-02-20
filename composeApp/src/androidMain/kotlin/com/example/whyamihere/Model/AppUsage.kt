package com.example.whyamihere.Model

data class AppUsage(
    val appName: String,
    val packageName: String = "",
    val timeUsed: Long
)
