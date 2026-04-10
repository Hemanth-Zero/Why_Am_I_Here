package com.example.whyamihere.View

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.ViewModel.MyAppViewModel

// ── Settings Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    myAppViewModel: MyAppViewModel,
    onBack: () -> Unit
) {
    var breakMins      by remember { mutableIntStateOf(myAppViewModel.getBreakReminderMinutes()) }
    var showDialog     by remember { mutableStateOf(false) }
    var breakInputText by remember { mutableStateOf(breakMins.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Reminders section ──────────────────────────────────────────────
            item {
                SectionHeader("Reminders")
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Break interval", style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium)
                            Text("Overlay button 2 will set a $breakMins-min break",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { showDialog = true }) { Text("Edit") }
                    }
                }
            }

            // ── About section ──────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader("About")
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Why Am I Here?", fontWeight = FontWeight.Bold)
                        Text(
                            "An app that helps you be mindful of your digital habits. " +
                            "The overlay appears only when today's (IST 00:00–24:00) usage " +
                            "exceeds your set daily limit.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Version 2.0", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // ── Edit break interval dialog ─────────────────────────────────────────
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title            = { Text("Break Interval") },
                text             = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("How many minutes should button 2 on the overlay use?")
                        OutlinedTextField(
                            value         = breakInputText,
                            onValueChange = { breakInputText = it.filter(Char::isDigit) },
                            label         = { Text("Minutes") },
                            singleLine    = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val mins = breakInputText.toIntOrNull()?.coerceIn(1, 120) ?: 30
                        breakMins = mins
                        myAppViewModel.setBreakReminderMinutes(mins)
                        showDialog = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

// ── Themes & Preferences Screen ───────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemesScreen(
    myAppViewModel: MyAppViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme & Preferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Appearance section ─────────────────────────────────────────────
            item { SectionHeader("Appearance") }

            // Theme mode selector
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.DarkMode, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Text("Theme Mode", style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium)
                        }

                        // Segmented button row
                        ThemeModeSelector(
                            current  = myAppViewModel.themeMode,
                            onChange = { myAppViewModel.setThemeMode(it) }
                        )
                    }
                }
            }

            // Dynamic color — Android 12+ only
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Row(
                            modifier          = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ColorLens, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dynamic Color", style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium)
                                Text("Use your wallpaper colors (Android 12+)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked         = myAppViewModel.dynamicColor,
                                onCheckedChange = { myAppViewModel.setDynamicColor(it) }
                            )
                        }
                    }
                }
            }

            // ── Display section ────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader("Display")
            }

            // Font scale slider
            item {
                FontScaleCard(
                    scale    = myAppViewModel.fontScale,
                    onChange = { myAppViewModel.setFontScale(it) }
                )
            }

            // Preview card
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader("Preview")
            }

            item {
                ThemePreviewCard(myAppViewModel)
            }
        }
    }
}

// ── Theme mode segmented selector ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSelector(current: String, onChange: (String) -> Unit) {
    val options = listOf(
        Pair("system", "System"),
        Pair("light",  "Light"),
        Pair("dark",   "Dark")
    )

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (key, label) ->
            SegmentedButton(
                shape    = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick  = { onChange(key) },
                selected = current == key,
                label    = { Text(label) },
                icon     = {}
            )
        }
    }
}

// ── Font scale card ───────────────────────────────────────────────────────────

@Composable
private fun FontScaleCard(scale: Float, onChange: (Float) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.TextFields, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Text("Font Size", style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                val label = when {
                    scale <= 0.9f  -> "Small"
                    scale <= 1.05f -> "Default"
                    scale <= 1.15f -> "Large"
                    else           -> "Largest"
                }
                Text(label, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
            }

            Slider(
                value        = scale,
                onValueChange = onChange,
                valueRange   = 0.85f..1.3f,
                steps        = 8,
                modifier     = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("A", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("A", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ── Preview card ──────────────────────────────────────────────────────────────

@Composable
private fun ThemePreviewCard(vm: MyAppViewModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Palette, contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint     = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                text       = "Theme preview",
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text  = buildString {
                    append("Mode: ${vm.themeMode.replaceFirstChar { it.uppercase() }}")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        append("  •  Dynamic: ${if (vm.dynamicColor) "On" else "Off"}")
                    }
                    val sizeLabel = when {
                        vm.fontScale <= 0.9f  -> "Small"
                        vm.fontScale <= 1.05f -> "Default"
                        vm.fontScale <= 1.15f -> "Large"
                        else                  -> "Largest"
                    }
                    append("  •  Font: $sizeLabel")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
            )
        }
    }
}

// ── Shared ────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(2.dp))
}
