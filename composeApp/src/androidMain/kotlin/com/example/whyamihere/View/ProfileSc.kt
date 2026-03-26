package com.example.whyamihere.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whyamihere.ViewModel.MyAppViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ProfileScreen(
    Sc1: () -> Unit, Sc2: () -> Unit, Sc3: () -> Unit,
    myAppViewModel: MyAppViewModel
) {
    val profileNavController = rememberNavController()

    NavHost(navController = profileNavController, startDestination = ProfileScreens.Profile.name) {
        composable(ProfileScreens.Profile.name) {
            Profile(
                Sc3 = Sc3, Sc1 = Sc1, Sc2 = Sc2,
                changeScreen = { id ->
                    when (id) {
                        0 -> profileNavController.navigate(ProfileScreens.Settings.name)
                        1 -> profileNavController.navigate(ProfileScreens.Statistics.name)
                        2 -> profileNavController.navigate(ProfileScreens.TrackedApps.name)
                        //3 -> profileNavController.navigate(ProfileScreens.Themes.name)
                        4 -> profileNavController.navigate(ProfileScreens.About.name)
                        5 -> profileNavController.navigate(ProfileScreens.Help.name)
                    }
                }
            )
        }
        composable(ProfileScreens.Settings.name) {
            SettingsScreen(
                myAppViewModel = myAppViewModel,
                onBack = { profileNavController.popBackStack() }
            )
        }
        composable(ProfileScreens.Statistics.name) {
            StatisticsScreen(
                myAppViewModel = myAppViewModel,
                onBack = { profileNavController.popBackStack() }
            )
        }
        composable(ProfileScreens.TrackedApps.name) {
            TrackedAppScreen(
                myAppViewModel = myAppViewModel,
                onBack = { profileNavController.popBackStack() }
            )
        }
//        composable(ProfileScreens.Themes.name) {
//            PlaceholderScreen(title = "Theme & Preferences", onBack = { profileNavController.popBackStack() })
//        }

        composable(ProfileScreens.Help.name) {
            HelpScreen()
        }
        composable(route = ProfileScreens.About.name){
            AboutScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Coming soon", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    Sc1: () -> Unit, Sc2: () -> Unit, Sc3: () -> Unit,
    changeScreen: (id: Int) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("My Profile") }) },
        bottomBar = { AppBottomBar(Sc1, Sc2 = Sc2, Sc3 = Sc3, selected = Screens.ProfileScreen.id) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }
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
                ProfileBody(changeScreen = { id -> changeScreen(id) })
            }
        }
    }
}

@Composable
fun ProfileCard(name: String, email: String, age: Int, location: String, phone: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(96.dp))
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Email, contentDescription = null); Text("email: $email")
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Cake, contentDescription = null); Text("Age: $age")
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null); Text(location)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Phone, contentDescription = null); Text(phone)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun ProfileBody(changeScreen: (id: Int) -> Unit) {
    val menuItems = listOf(
        Triple("Settings", Icons.Default.Settings, 0),
        Triple("Your Statistics", Icons.Default.BarChart, 1),
        Triple("Your Tracked Apps", Icons.Default.Apps, 2),
        //Triple("Theme & Preferences", Icons.Default.Palette, 3),
        Triple("About", Icons.Default.Info, 4),
        Triple("Help", Icons.Default.QuestionMark, 5),
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(menuItems) { item ->
            AnimatedProfileCard(title = item.first, icon = item.second, index = item.third, changeScreen = changeScreen)
        }
    }
}

@Composable
fun AnimatedProfileCard(title: String, icon: ImageVector, index: Int, changeScreen: (id: Int) -> Unit) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 125L); visible.value = true }
    AnimatedVisibility(
        visible = visible.value,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(72.dp).clickable { changeScreen(index) }
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 18.sp)
            }
        }
    }
}

enum class ProfileScreens(name: String, id: Int = -1) {
    Profile(name = "Profile"),
    Settings(name = "Settings", id = 0),
    Statistics(name = "Statistics", id = 1),
    TrackedApps(name = "TrackedApp", id = 2),
    //Themes(name = "Themes", id = 3),
    About(name = "About", id = 4),
    Help(name = "Help", id = 5)
}
