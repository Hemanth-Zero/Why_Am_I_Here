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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.Model.UsageStatsRepository
import com.example.whyamihere.ViewModel.MyAppViewModel
import java.text.SimpleDateFormat
import java.util.*

val barColors = listOf(
    Color(0xFFF0B3E8), Color(0xFFEFB4E9), Color(0xFFF4B2E3),
    Color(0xFFDDA0DD), Color(0xFFBA55D3), Color(0xFF9370DB),
)

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    myAppViewModel: MyAppViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var dayOffset   by remember { mutableIntStateOf(0) }
    val repo        = remember { UsageStatsRepository(myAppViewModel.getContext()) }

    val dailyList = remember(dayOffset) { repo.getUsageForDay(dayOffset) }

    val dateLabel = remember(dayOffset) {
        val ist = TimeZone.getTimeZone("Asia/Kolkata")
        val cal = Calendar.getInstance(ist)
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)
        when (dayOffset) {
            0  -> "Today"
            -1 -> "Yesterday"
            else -> SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).apply {
                timeZone = ist
            }.format(cal.time)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // ── Tab Row ────────────────────────────────────────────────────────
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = { Text("Daily") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = { Text("Weekly") }
                )
            }

            when (selectedTab) {
                0 -> {
                    // ── Daily ──────────────────────────────────────────────────
                    DayNavigationBar(
                        label        = dateLabel,
                        canGoBack    = dayOffset > -6,
                        canGoForward = dayOffset < 0,
                        onPrev       = { dayOffset-- },
                        onNext       = { dayOffset++ }
                    )
                    HorizontalDivider()
                    DailyStatsContent(usageList = dailyList)
                }
                1 -> {
                    // ── Weekly ─────────────────────────────────────────────────
                    WeeklyStatsContent(repo = repo)
                }
            }
        }
    }
}

// ── Day navigation bar ────────────────────────────────────────────────────────

@Composable
private fun DayNavigationBar(
    label        : String,
    canGoBack    : Boolean,
    canGoForward : Boolean,
    onPrev       : () -> Unit,
    onNext       : () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrev, enabled = canGoBack) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous day",
                tint = if (canGoBack) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        Text(
            text       = label,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNext, enabled = canGoForward) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next day",
                tint = if (canGoForward) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

// ── Daily stats ───────────────────────────────────────────────────────────────

@Composable
fun DailyStatsContent(usageList: List<AppUsage>) {
    val sorted  = usageList.sortedByDescending { it.timeUsed }.take(8)
    val maxTime = sorted.maxOfOrNull { it.timeUsed } ?: 1L
    val total   = sorted.sumOf { it.timeUsed }

    if (sorted.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No usage data for this day", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SummaryCard(totalTime = total, label = "Total screen time")
            Spacer(Modifier.height(8.dp))
            Text("Top apps", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        itemsIndexed(sorted) { index, app ->
            UsageBarCard(app = app, maxTime = maxTime, color = barColors[index % barColors.size], index = index)
        }
    }
}

// ── Weekly stats ──────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WeeklyStatsContent(repo: UsageStatsRepository) {
    // Build last-7-days data: index 0 = 6 days ago … index 6 = today
    val ist = TimeZone.getTimeZone("Asia/Kolkata")
    val dayFmt = SimpleDateFormat("EEE", Locale.getDefault()).apply { timeZone = ist }

    data class DayData(val label: String, val totalMs: Long)

    val weekData: List<DayData> = remember {
        (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance(ist).apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
            val label = dayFmt.format(cal.time)
            val usage = repo.getUsageForDay(-daysAgo)
            DayData(label, usage.sumOf { it.timeUsed })
        }
    }

    val weekTotal  = weekData.sumOf { it.totalMs }
    val maxDayMs   = weekData.maxOfOrNull { it.totalMs } ?: 1L

    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }

    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryCard(totalTime = weekTotal, label = "Total this week")
        }

        item {
            Text(
                "Daily breakdown",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Bar chart card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Bar chart
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        verticalAlignment     = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekData.forEachIndexed { index, day ->
                            val targetFraction = if (maxDayMs > 0) day.totalMs.toFloat() / maxDayMs else 0f
                            val animatedFraction by animateFloatAsState(
                                targetValue   = if (animated) targetFraction else 0f,
                                animationSpec = tween(600, delayMillis = index * 80),
                                label         = "weekBar$index"
                            )
                            WeeklyBarColumn(
                                fraction = animatedFraction,
                                label    = day.label,
                                color    = barColors[index % barColors.size],
                                isToday  = index == 6,
                                totalMs  = day.totalMs
                            )
                        }
                    }
                }
            }
        }

        // Per-day breakdown rows
        item {
            Text(
                "Per day details",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        itemsIndexed(weekData.reversed()) { index, day ->
            WeekDayRow(day.label, day.totalMs, barColors[index % barColors.size])
        }
    }
}

@Composable
private fun WeeklyBarColumn(
    fraction : Float,
    label    : String,
    color    : Color,
    isToday  : Boolean,
    totalMs  : Long
) {
    val hrs  = totalMs / (1000 * 60 * 60)
    val mins = (totalMs / (1000 * 60)) % 60

    Column(
        modifier            = Modifier.width(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Time label above bar
        if (totalMs > 0) {
            Text(
                text      = if (hrs > 0) "${hrs}h" else "${mins}m",
                fontSize  = 9.sp,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(2.dp))
        }

        // Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val barColor = if (isToday) color else color.copy(alpha = 0.6f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barHeight = size.height * fraction
                drawRoundRect(
                    color        = barColor,
                    topLeft      = Offset(0f, size.height - barHeight),
                    size         = Size(size.width, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text      = label,
            fontSize  = 10.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color      = if (isToday) MaterialTheme.colorScheme.primary
                         else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign  = TextAlign.Center
        )
    }
}

@Composable
private fun WeekDayRow(label: String, totalMs: Long, color: Color) {
    val hrs    = totalMs / (1000 * 60 * 60)
    val mins   = (totalMs / (1000 * 60)) % 60
    val timeStr = when {
        totalMs == 0L -> "No usage"
        hrs > 0       -> "${hrs}h ${mins}m"
        else          -> "${mins}m"
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .let {
                        it.then(
                            Modifier.padding(end = 0.dp)
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = color)
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text      = label,
                modifier  = Modifier.weight(1f),
                style     = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text  = timeStr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Shared composables ────────────────────────────────────────────────────────

@Composable
fun SummaryCard(totalTime: Long, label: String) {
    val hrs  = totalTime / (1000 * 60 * 60)
    val mins = (totalTime / (1000 * 60)) - hrs * 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "${hrs}h ${mins}m",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun UsageBarCard(app: AppUsage, maxTime: Long, color: Color, index: Int) {
    var animTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(index * 80L); animTrigger = true }

    val animatedProgress by animateFloatAsState(
        targetValue   = if (animTrigger) app.timeUsed.toFloat() / maxTime.toFloat() else 0f,
        animationSpec = tween(600),
        label         = "bar"
    )

    val hrs     = app.timeUsed / (1000 * 60 * 60)
    val mins    = (app.timeUsed / (1000 * 60)) - hrs * 60
    val timeStr = if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = app.appName,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.weight(1f)
                )
                Text(
                    text  = timeStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color        = Color.White.copy(alpha = 0.08f),
                        size         = size,
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    drawRoundRect(
                        color        = color,
                        size         = Size(size.width * animatedProgress, size.height),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
    }
}
