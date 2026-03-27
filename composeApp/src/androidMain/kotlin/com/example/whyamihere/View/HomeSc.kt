package com.example.whyamiherelab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.View.AppBottomBar
import com.example.whyamihere.View.Screens
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    listofapp: List<AppUsage>,
    Sc1: () -> Unit,
    Sc2: () -> Unit,
    Sc3: () -> Unit
) {
    val sortedList = listofapp.sortedByDescending { it.timeUsed }
    val totalTime = sortedList.sumOf { it.timeUsed }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "HomeScreen") },
            )
        },
        bottomBar = {
            AppBottomBar(
                Sc1,
                Sc2 = Sc2,
                Sc3 = Sc3,
                selected = Screens.HomeScreen.id
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {

            val screenHeight = LocalConfiguration.current.screenWidthDp.dp


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .height(screenHeight / 1.5f)
            ) {
                MultColorCircularBar(sortedList)
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    sortedList.take(4).forEachIndexed { index, app ->
                        AppCard(app = app, i = index, totalTime = totalTime)
                    }
                }
            }
        }
    }
}

@Composable
fun AppCard(app: AppUsage, i: Int, totalTime: Long) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(i * 100L)
        visible = true
    }

    val (hrs, mins) = hournMin(app.timeUsed)
    val progress =
        if (totalTime > 0) app.timeUsed.toFloat() / totalTime else 0f

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {

            Column(modifier = Modifier.padding(12.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(
                                color = when (i) {
                                    0 -> Color.Red
                                    1 -> Color.Yellow
                                    2 -> Color.Green
                                    3 -> Color.Magenta
                                    else -> Color.Blue
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = app.appName)
                    }

                    Text(text = "${hrs}h ${mins}m")
                }

                Spacer(modifier = Modifier.height(8.dp))


                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
            }
        }
    }
}

@Composable
fun MultColorCircularBar(usagedata: List<AppUsage>) {

    val totaltime = usagedata.sumOf { it.timeUsed }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {

            val screenWidth = LocalConfiguration.current.screenWidthDp.dp / 2

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                val (hrs, mins) = hournMin(totaltime)

                Text(
                    text = "Total time\n${hrs} hrs ${mins} mins",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Canvas(modifier = Modifier.size(screenWidth)) {

                    var startAngle = -90f

                    usagedata.take(5).forEachIndexed { i, app ->

                        val color = when (i) {
                            0 -> Color.Red
                            1 -> Color.Yellow
                            2 -> Color.Green
                            3 -> Color.Magenta
                            else -> Color.Blue
                        }

                        val sweepAngle =
                            if (totaltime > 0)
                                360f * (app.timeUsed.toFloat() / totaltime.toFloat())
                            else 0f

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            style = Stroke(width = 15f, cap = StrokeCap.Round),
                            useCenter = false
                        )

                        startAngle += sweepAngle
                    }
                }
            }
        }
    }
}

fun hournMin(time: Long): Pair<Long, Long> {
    val hour = time / (1000 * 60 * 60)
    val mins = (time / (1000 * 60)) - hour * 60
    return Pair(hour, mins)
}

@Composable
fun UsageScreen(
    usageList: List<AppUsage>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "App Usage",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(usageList) { app ->
                UsageItem(app)
            }
        }
    }
}

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
            Text(text = app.packageName)
            Text(text = formatTime(app.timeUsed))
        }
    }
}

fun formatTime(timeMillis: Long): String {
    val minutes = timeMillis / 60000
    return "$minutes min"
}