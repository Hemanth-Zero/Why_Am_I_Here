package com.example.whyamihere.View

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun AppBottomBar( Sc1:()-> Unit , Sc2:()->  Unit , Sc3:()-> Unit ,selected : Int) {

    NavigationBar {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            BottomBarItem(
                selected = selected == 1,
                onClick = { if(selected!=1)Sc1() },
                icon = Icons.Default.List,
                label = "Your Tasks"
            )
            BottomBarItem(
                selected = selected == 2,
                onClick = { if(selected!=2) Sc2() },
                icon = Icons.Default.Home,
                label = "Home"
            )
            BottomBarItem(
                selected = selected == 3,
                onClick = { if(selected!=3)Sc3() },
                icon = Icons.Default.Person,
                label = "Profile"
            )
        }
    }
}



@Composable
fun BottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1f,
        label = ""
    )
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.scale(scale)
                )

            }
        },
        label = {Text(text = label)},
        modifier = Modifier.padding(4.dp)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorScreen(){
    Box(modifier = Modifier.fillMaxSize()){
        Column(verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally) {
            CircularWavyProgressIndicator()
        }
    }
}


