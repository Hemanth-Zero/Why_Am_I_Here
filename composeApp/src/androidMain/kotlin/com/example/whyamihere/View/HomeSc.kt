package com.example.whyamiherelab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

fun HomeScreen( listofapp : List<AppUsage> ,
                Sc1:()-> Unit,Sc2: () -> Unit , Sc3: () -> Unit
                ){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "HomeScreen") },
            ) },
        bottomBar = {
            AppBottomBar(Sc1, Sc2 = Sc2, Sc3 = Sc3 , selected = Screens.HomeScreen.id)
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
fun AppCard(app: AppUsage , i : Int ){
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(i*100L)
        visible = true
    }
    AnimatedVisibility(visible = visible
    , enter = slideInHorizontally(initialOffsetX = {it})+ fadeIn()
    ) {
        Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

                var apptime = ""
                if (hournMin(app.timeUsed)[0] > 0) {
                    apptime += "" + hournMin(app.timeUsed)[0] + "hrs"
                }
                apptime += " " + hournMin(app.timeUsed)[1] + "mins"
                Text(text = apptime)
            }
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