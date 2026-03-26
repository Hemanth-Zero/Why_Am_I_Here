package com.example.whyamihere.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.whyamihere.ViewModel.AppData
import com.example.whyamihere.ViewModel.MyAppViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackedAppScreen(myAppViewModel: MyAppViewModel, onBack: () -> Unit = {}) {
    val installedApps = remember { myAppViewModel.getNonSystemApps() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredApps = installedApps.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val enabledApps = remember {
        mutableStateMapOf<String, Boolean>().apply {
            installedApps.forEach { put(it.packageName, it.onTrack) }
        }
    }
    val dailyLimits = remember {
        mutableStateMapOf<String, Int>().apply {
            installedApps.forEach { app ->
                put(app.packageName, myAppViewModel.getDailyLimitMinutes(app.packageName))
            }
        }
    }
    var showLimitDialog by remember { mutableStateOf<String?>(null) }
    var limitInputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tracked Apps") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val tracked = enabledApps.filter { it.value }.keys.toSet()
                        myAppViewModel.saveTrackedApps(tracked)
                        dailyLimits.forEach { (pkg, mins) ->
                            myAppViewModel.setDailyLimitMinutes(pkg, mins)
                        }
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply & Save")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Search apps") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            val trackedFiltered = filteredApps.filter { enabledApps[it.packageName] == true }
            val untrackedFiltered = filteredApps.filter { enabledApps[it.packageName] != true }

            if (trackedFiltered.isNotEmpty()) {
                item {
                    Text(
                        "Tracked (${trackedFiltered.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                items(trackedFiltered, key = { it.packageName }) { app ->
                    TrackedAppCard(
                        app = app,
                        enabled = enabledApps[app.packageName] == true,
                        dailyLimitMinutes = dailyLimits[app.packageName] ?: 60,
                        onToggle = { enabledApps[app.packageName] = it },
                        onEditLimit = {
                            limitInputText = (dailyLimits[app.packageName] ?: 60).toString()
                            showLimitDialog = app.packageName
                        }
                    )
                }
            }

            if (untrackedFiltered.isNotEmpty()) {
                item {
                    Text(
                        "Other apps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                items(untrackedFiltered, key = { it.packageName }) { app ->
                    TrackedAppCard(
                        app = app,
                        enabled = enabledApps[app.packageName] == true,
                        dailyLimitMinutes = dailyLimits[app.packageName] ?: 60,
                        onToggle = { enabledApps[app.packageName] = it },
                        onEditLimit = {
                            limitInputText = (dailyLimits[app.packageName] ?: 60).toString()
                            showLimitDialog = app.packageName
                        }
                    )
                }
            }
        }
    }

    showLimitDialog?.let { pkg ->
        val appName = installedApps.find { it.packageName == pkg }?.name ?: pkg
        AlertDialog(
            onDismissRequest = { showLimitDialog = null },
            title = { Text("Daily Limit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Set a daily time limit for $appName")
                    OutlinedTextField(
                        value = limitInputText,
                        onValueChange = { limitInputText = it.filter { c -> c.isDigit() } },
                        label = { Text("Minutes per day") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val mins = limitInputText.toIntOrNull()?.coerceIn(1, 1440) ?: 60
                    dailyLimits[pkg] = mins
                    showLimitDialog = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun TrackedAppCard(
    app: AppData,
    enabled: Boolean,
    dailyLimitMinutes: Int,
    onToggle: (Boolean) -> Unit,
    onEditLimit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (enabled) 4.dp else 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberDrawablePainter(app.icon),
                    contentDescription = app.name,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(app.name, fontWeight = FontWeight.Medium)
                    if (enabled) {
                        Text(
                            "Limit: ${dailyLimitMinutes}m/day",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            if (enabled) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                TextButton(
                    onClick = onEditLimit,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp, bottom = 4.dp)
                ) {
                    Text("Set daily limit")
                }
            }
        }
    }
}
