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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                myAppViewModel = myAppViewModel,
                changeScreen = { id ->
                    when (id) {
                        0 -> profileNavController.navigate(ProfileScreens.Settings.name)
                        1 -> profileNavController.navigate(ProfileScreens.Statistics.name)
                        2 -> profileNavController.navigate(ProfileScreens.TrackedApps.name)
                        3 -> profileNavController.navigate(ProfileScreens.Themes.name)
                        4 -> profileNavController.navigate(ProfileScreens.About.name)
                        5 -> profileNavController.navigate(ProfileScreens.Help.name)
                    }
                }
            )
        }
        composable(ProfileScreens.Settings.name) {
            SettingsScreen(myAppViewModel = myAppViewModel, onBack = { profileNavController.popBackStack() })
        }
        composable(ProfileScreens.Statistics.name) {
            StatisticsScreen(myAppViewModel = myAppViewModel, onBack = { profileNavController.popBackStack() })
        }
        composable(ProfileScreens.TrackedApps.name) {
            TrackedAppScreen(myAppViewModel = myAppViewModel, onBack = { profileNavController.popBackStack() })
        }
        composable(ProfileScreens.Themes.name) {
            ThemesScreen(myAppViewModel = myAppViewModel, onBack = { profileNavController.popBackStack() })
        }
        composable(ProfileScreens.Help.name) {
            HelpScreen()
        }
        composable(route = ProfileScreens.About.name) {
            AboutScreen()
        }
    }
}

// ── Placeholder (kept for safety) ─────────────────────────────────────────────

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

// ── Profile root screen ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    Sc1: () -> Unit, Sc2: () -> Unit, Sc3: () -> Unit,
    myAppViewModel: MyAppViewModel,
    changeScreen: (id: Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar         = { TopAppBar(title = { Text("My Profile") }) },
        bottomBar      = { AppBottomBar(Sc1, Sc2 = Sc2, Sc3 = Sc3, selected = Screens.ProfileScreen.id) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier            = Modifier.padding(paddingValues).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }

                AnimatedVisibility(
                    visible = visible,
                    enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit    = fadeOut()
                ) {
                    ProfileCard(
                        name     = myAppViewModel.profileName,
                        email    = myAppViewModel.profileEmail,
                        age      = myAppViewModel.profileAge,
                        location = myAppViewModel.profileLocation,
                        phone    = myAppViewModel.profilePhone,
                        onEdit   = { showEditDialog = true }
                    )
                }

                ProfileBody(changeScreen = { id -> changeScreen(id) })
            }
        }
    }

    // ── Edit dialog ────────────────────────────────────────────────────────────
    if (showEditDialog) {
        ProfileEditDialog(
            initialName     = myAppViewModel.profileName,
            initialEmail    = myAppViewModel.profileEmail,
            initialAge      = myAppViewModel.profileAge,
            initialLocation = myAppViewModel.profileLocation,
            initialPhone    = myAppViewModel.profilePhone,
            onDismiss       = { showEditDialog = false },
            onSave          = { name, email, age, location, phone ->
                myAppViewModel.saveProfile(name, email, age, location, phone)
                showEditDialog = false
            }
        )
    }
}

// ── Profile card ──────────────────────────────────────────────────────────────

@Composable
fun ProfileCard(
    name     : String,
    email    : String,
    age      : Int,
    location : String,
    phone    : String,
    onEdit   : () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(96.dp))
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Text("email: $email")
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Cake, contentDescription = null)
                    Text("Age: $age")
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Text(location)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text(phone)
                }
            }

            // Edit button — tappable
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(
                    modifier          = Modifier.clickable(onClick = onEdit).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit profile",
                        modifier = Modifier.size(20.dp),
                        tint     = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Edit",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ── Edit dialog ───────────────────────────────────────────────────────────────

@Composable
fun ProfileEditDialog(
    initialName     : String,
    initialEmail    : String,
    initialAge      : Int,
    initialLocation : String,
    initialPhone    : String,
    onDismiss       : () -> Unit,
    onSave          : (name: String, email: String, age: Int, location: String, phone: String) -> Unit
) {
    var name     by remember { mutableStateOf(initialName) }
    var email    by remember { mutableStateOf(initialEmail) }
    var ageText  by remember { mutableStateOf(initialAge.toString()) }
    var location by remember { mutableStateOf(initialLocation) }
    var phone    by remember { mutableStateOf(initialPhone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Edit Profile") },
        text             = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    OutlinedTextField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = { Text("Name") },
                        leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Email") },
                        leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value         = ageText,
                        onValueChange = { ageText = it.filter(Char::isDigit) },
                        label         = { Text("Age") },
                        leadingIcon   = { Icon(Icons.Default.Cake, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value         = location,
                        onValueChange = { location = it },
                        label         = { Text("Location") },
                        leadingIcon   = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value         = phone,
                        onValueChange = { phone = it },
                        label         = { Text("Phone") },
                        leadingIcon   = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val age = ageText.toIntOrNull()?.coerceIn(1, 120) ?: initialAge
                onSave(name.trim(), email.trim(), age, location.trim(), phone.trim())
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ── Profile body (menu list) ──────────────────────────────────────────────────

@Composable
fun ProfileBody(changeScreen: (id: Int) -> Unit) {
    val menuItems = listOf(
        Triple("Settings",          Icons.Default.Settings,     0),
        Triple("Your Statistics",   Icons.Default.BarChart,     1),
        Triple("Your Tracked Apps", Icons.Default.Apps,         2),
        Triple("Theme & Preferences", Icons.Default.Palette,   3),
        Triple("About",             Icons.Default.Info,         4),
        Triple("Help",              Icons.Default.QuestionMark, 5),
    )
    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(menuItems) { item ->
            AnimatedProfileCard(
                title        = item.first,
                icon         = item.second,
                index        = item.third,
                changeScreen = changeScreen
            )
        }
    }
}

@Composable
fun AnimatedProfileCard(title: String, icon: ImageVector, index: Int, changeScreen: (id: Int) -> Unit) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 100L); visible.value = true }
    AnimatedVisibility(
        visible = visible.value,
        enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit    = fadeOut()
    ) {
        Card(modifier = Modifier.fillMaxWidth().height(72.dp).clickable { changeScreen(index) }) {
            Row(
                modifier          = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 18.sp)
            }
        }
    }
}

// ── Enums ─────────────────────────────────────────────────────────────────────

enum class ProfileScreens(name: String, id: Int = -1) {
    Profile(name = "Profile"),
    Settings(name = "Settings",       id = 0),
    Statistics(name = "Statistics",   id = 1),
    TrackedApps(name = "TrackedApp",  id = 2),
    Themes(name = "Themes",           id = 3),
    About(name = "About",             id = 4),
    Help(name = "Help",               id = 5)
}
