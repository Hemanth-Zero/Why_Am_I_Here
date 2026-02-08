package com.example.whyamihere.View

import com.example.whyamihere.Model.AppUsage
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun UsageItem(app: AppUsage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = app.appName)
            Text(text = formatTime(app.timeUsed))
        }
    }
}

fun formatTime(timeMillis: Long): String {
    val minutes = timeMillis / 60000
    return "$minutes min"
}

