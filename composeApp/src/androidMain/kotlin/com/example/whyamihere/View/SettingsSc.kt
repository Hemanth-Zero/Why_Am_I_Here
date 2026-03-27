package com.example.whyamihere.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whyamihere.ViewModel.MyAppViewModel

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
            item {
                Text(
                    "Reminders",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }

            // Break interval card
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Break interval",
                                style      = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                // This value is what button 2 of the overlay uses
                                "Overlay button 2 will set a $breakMins-min break",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { showDialog = true }) { Text("Edit") }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "About",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                        Text(
                            "Version 2.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        //  Edit break interval dialog
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
