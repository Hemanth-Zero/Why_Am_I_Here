package com.example.whyamihere.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ProfileScreen(Sc1:()-> Unit,Sc2: () -> Unit , Sc3: () -> Unit , Sc4:()->Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text(text = "My Profile") }, actions = {}) },
        bottomBar = { AppBottomBar(Sc1, Sc2 = Sc2, Sc3 = Sc3 , selected = Screens.ProfileScreen.id) },

    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(timeMillis = 0.0001f.toLong())
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = fadeOut()
                ) {
                    ProfileCard(
                        name = "Hemanth",
                        email = "hemanth@123.gmail.com",
                        age = 20,
                        location = "Bangalore",
                        phone = "+91 9876543210"
                    )
                }
                ProfileBody()

            }
        }
    }

}

@Composable
fun ProfileCard(
    name: String,
    email: String,
    age: Int,
    location: String,
    phone: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Image",
                modifier = Modifier.size(96.dp)
            )

            Text(text = name)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Text(text = "email : "+ email)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Cake, contentDescription = null)
                    Text(text = "Age: $age")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Text(text = location)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text(text = phone)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileBody() {
    val items = listOf(
        Triple("Settings", Icons.Default.Settings, 0),
        Triple("Your Statistics", Icons.Default.BarChart, 1),
        Triple("Your Tracked Apps", Icons.Default.Apps, 2),
        Triple("Theme & Preferences", Icons.Default.Palette, 3),
        Triple("About", Icons.Default.Info, 4),
        Triple("Help", Icons.Default.Help, 5)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            AnimatedProfileCard(
                title = item.first,
                icon = item.second,
                index = item.third
            )
        }
    }
}

@Composable
fun AnimatedProfileCard(
    title: String,
    icon: ImageVector,
    index: Int
) {
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 125L)
        visible.value = true
    }

    AnimatedVisibility(
        visible = visible.value,
        enter = slideInVertically(
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(onClick = {  } )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 18.sp
                )
            }
        }
    }
}