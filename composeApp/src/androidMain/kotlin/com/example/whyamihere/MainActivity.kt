package com.example.whyamihere

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi

import com.example.compose.AppTheme
import com.example.whyamihere.Model.UsageStatsRepository

import com.example.whyamiherelab.HomeScreen

class MainActivity : ComponentActivity() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onCreate(savedInstanceState: Bundle?) {
            enableEdgeToEdge()
            super.onCreate(savedInstanceState)
            val usageRepository = UsageStatsRepository(this)
            val usageList = usageRepository.getTodayUsage()
            setContent {
                AppTheme(darkTheme = true, dynamicColor = false) {
                    val sortedList = usageList.sortedByDescending { it.timeUsed }
                    HomeScreen(sortedList)

                }
            }
        }
}

