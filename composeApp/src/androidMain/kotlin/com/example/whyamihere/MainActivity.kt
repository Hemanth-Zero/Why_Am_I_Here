package com.example.whyamihere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.whyamihere.Model.UsageStatsRepository
import com.example.whyamihere.View.UsageScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val usageRepository = UsageStatsRepository(this)

        val usageList = usageRepository.getLast24HoursUsage()

        setContent {
            val sortedList = usageList.sortedByDescending { it.timeUsed }
            UsageScreen(sortedList)
        }
    }
}

