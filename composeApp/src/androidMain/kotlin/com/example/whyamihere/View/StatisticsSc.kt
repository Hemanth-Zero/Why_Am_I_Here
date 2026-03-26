package com.example.whyamihere.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.ViewModel.MyAppViewModel

val barColors = listOf(
    Color(0xFFF0B3E8),
    Color(0xFFEFB4E9),
    Color(0xFFF4B2E3),
    Color(0xFFDDA0DD),
    Color(0xFFBA55D3),
    Color(0xFF9370DB),
)

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    myAppViewModel: MyAppViewModel,
    onBack: () -> Unit
) {
    val dailyList = remember { myAppViewModel.getUsageList() }
    val weeklyMap = remember { myAppViewModel.getWeeklyUsage() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Daily", "Weekly")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> DailyStatsContent(dailyList)
                1 -> WeeklyStatsContent(weeklyMap)
            }
        }
    }
}

@Composable
fun DailyStatsContent(usageList: List<AppUsage>) {
    val sorted = usageList.sortedByDescending { it.timeUsed }.take(8)
    val maxTime = sorted.maxOfOrNull { it.timeUsed } ?: 1L
    val totalTime = sorted.sumOf { it.timeUsed }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SummaryCard(totalTime = totalTime, label = "Total screen time today")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Top apps today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        itemsIndexed(sorted) { index, app ->
            UsageBarCard(app = app, maxTime = maxTime, color = barColors[index % barColors.size], index = index)
        }
    }
}

@Composable
fun WeeklyStatsContent(weeklyMap: Map<String, Long>) {
    val sorted = weeklyMap.entries.sortedByDescending { it.value }.take(8)
    val maxTime = sorted.maxOfOrNull { it.value } ?: 1L
    val totalTime = sorted.sumOf { it.value }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SummaryCard(totalTime = totalTime, label = "Total screen time this week")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Top apps this week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        itemsIndexed(sorted) { index, entry ->
            val app = com.example.whyamihere.Model.AppUsage(
                appName = entry.key,
                timeUsed = entry.value
            )
            UsageBarCard(
                app = app,
                maxTime = maxTime,
                color = barColors[index % barColors.size],
                index = index
            )
        }
    }
}

@Composable
fun SummaryCard(totalTime: Long, label: String) {
    val hrs = totalTime / (1000 * 60 * 60)
    val mins = (totalTime / (1000 * 60)) - hrs * 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${hrs}h ${mins}m",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun UsageBarCard(app: AppUsage, maxTime: Long, color: Color, index: Int) {
    var animTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 80L)
        animTrigger = true
    }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animTrigger) app.timeUsed.toFloat() / maxTime.toFloat() else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "bar"
    )

    val hrs = app.timeUsed / (1000 * 60 * 60)
    val mins = (app.timeUsed / (1000 * 60)) - hrs * 60
    val timeStr = if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.08f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                    // Foreground
                    drawRoundRect(
                        color = color,
                        size = Size(size.width * animatedProgress, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
    }
}
