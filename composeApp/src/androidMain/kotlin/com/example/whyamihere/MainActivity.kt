package com.example.whyamihere

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi

import com.example.compose.AppTheme
import com.example.whyamihere.Model.UsageStatsRepository
import com.example.whyamihere.View.AppNavi
import com.example.whyamihere.ViewModel.createChannel

import com.example.whyamiherelab.HomeScreen
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onCreate(savedInstanceState: Bundle?) {
            createChannel(this)
            window.decorView.setBackgroundColor(android.graphics.Color.parseColor("#121212"))
            enableEdgeToEdge()
            super.onCreate(savedInstanceState)
            val usageRepository = UsageStatsRepository(this)
            setContent {
                AppTheme(darkTheme = true, dynamicColor = false) {
                    AppNavi(usageRepository)
                }
            }
        }
}

