package com.example.whyamiherelab

import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.borderStroke
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme
import com.example.whyamihere.Model.AppUsage
import com.example.whyamihere.View.UsageScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen( listofapp : List<AppUsage>){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "HomeScreen") },
            ) },
        bottomBar = {
            BottomAppBar() {
                Switch(
                    checked = true,
                    onCheckedChange = {}
                )
            }
        }
    ) { paddingValues ->
        var sortedList = listofapp.sortedByDescending { it.timeUsed }
        Column(modifier = Modifier.padding(paddingValues)) {
            val screenheight = LocalConfiguration.current.screenWidthDp.dp
            Card(
                modifier = Modifier.fillMaxWidth().padding(4.dp).height(screenheight/1.5f)
            ) {
                MultColorCircularBar(
                    sortedList
                )

            }
            Card(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                for( i in 0 until 5){
                    AppCard(app = sortedList.get(i) , i)
                }

            }
            UsageScreen(sortedList)
        }
    }
}

@Composable
fun AppCard(app: AppUsage , i : Int){
    Card( modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(
                        color = when (i) {
                            0 -> Color.Red
                            1 -> Color.Yellow
                            2 -> Color.Green
                            3 -> Color.Magenta
                            4 -> Color(0xFFFF9800)
                            else -> Color.Blue
                        },
                        radius = size.minDimension / 2
                    )
                }
                Text(text = app.appName, modifier = Modifier.padding(4.dp))
            }

            var apptime=""
            if(hournMin(app.timeUsed)[0]>0){
                apptime+=""+hournMin(app.timeUsed)[0]+"hrs"
            }
            apptime+=" "+hournMin(app.timeUsed)[1]+"mins"
            Text(text = apptime)
        }
    }
}


@Composable
fun MultColorCircularBar(usagedata: List<AppUsage>){
    val totaltime = usagedata.sumOf { it.timeUsed }
    Card(
        Modifier.fillMaxWidth().padding(10.dp) ,
        ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(10.dp))
        {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp/2
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                var timetodisplay = "Total time \n "+ hournMin(totaltime)[0]+" hrs "+ hournMin(totaltime)[1]+" mins"
                Text(text = timetodisplay , fontSize = 16.sp, fontWeight = FontWeight.Bold)

                var sweptangle = 0f
                Canvas(modifier = Modifier.size(screenWidth)) {
                    var startangle = -90f
                    for (i in 0 until 6) {
                        var color = when (i) {
                            0 -> Color.Red
                            1 -> Color.Yellow
                            2 -> Color.Green
                            3 -> Color.Magenta
                            4 -> Color(0xFFFF9800)
                            else -> Color.Blue
                        }
                        sweptangle =
                            360f * (usagedata.get(i).timeUsed.toFloat() / totaltime.toFloat())
                        if(i == 5){
                            sweptangle = 270f - startangle
                        }
                        drawArc(
                            color = color,
                            startAngle = startangle,
                            sweepAngle = sweptangle,
                            style = Stroke(width = 15f, cap = StrokeCap.Round),
                            useCenter = false,

                            )
                        startangle += sweptangle
                    }

                }
            }
        }
    }

}

fun hournMin(time: Long): List<Long>{
    var hour = (time/(1000*60*60))
    var mins = ((time/(1000*60))-hour*60)
    return listOf(hour,mins)
}