package com.example.whyamihere.View

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.Model.TaskDatabase
import com.example.whyamihere.Model.TrackedAppsPrefs


// Callbacks supplied by OverlayService
data class OverlayCallbacks(
    val onUnlimited   : () -> Unit,   // Button 1 – Unlimited access for today
    val onBreakInterval: () -> Unit,  // Button 2 – Break for "settings-defined" interval
    val onCustomTimer : (Int) -> Unit, // Button 3 – User-chosen 5-30 min timer
    val onExit        : () -> Unit    // Button 4 – Exit / go home
)

//mani over lay composition n
@Composable
fun IntentionOverlay(
    appName     : String,
    packageName : String,
    callbacks   : OverlayCallbacks
) {
    val context = LocalContext.current

    // Load tasks from DB (run once on composition)
    val tasks = remember {
        TaskDatabase.getInstance(context).getAllTasks()
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Custom-timer picker state
    var showTimerPicker by remember { mutableStateOf(false) }
    var chosenMinutes   by remember { mutableStateOf(15) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC0D0D0D)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter   = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .wrapContentHeight(),
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Text(
                        text       = "Why are you here?",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text       = "You've hit your daily limit on $appName.\nWhat do you want to do?",
                        style      = MaterialTheme.typography.bodyMedium,
                        textAlign  = TextAlign.Center,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    // ── Button 1 – Unlimited ───────────────────────────────
                    OverlayButton(
                        icon        = Icons.Default.AllInclusive,
                        label       = "Give me unlimited access",
                        description = "Dismiss for the rest of today",
                        tint        = Color(0xFF4CAF50),
                        onClick     = callbacks.onUnlimited
                    )

                    // ── Button 2 – Break (settings interval) ───────────────
                    val breakMins = remember {
                        TrackedAppsPrefs.getBreakReminderMinutes(context)
                    }
                    OverlayButton(
                        icon        = Icons.Default.Coffee,
                        label       = "Take a $breakMins-min break",
                        description = "Set in Settings → Break interval",
                        tint        = Color(0xFF2196F3),
                        onClick     = callbacks.onBreakInterval
                    )

                    // ── Button 3 – Custom timer (5-30 min) ────────────────
                    OverlayButton(
                        icon        = Icons.Default.Timer,
                        label       = "Custom break ($chosenMinutes min)",
                        description = "Pick between 5 and 30 minutes",
                        tint        = Color(0xFFFF9800),
                        onClick     = { showTimerPicker = true }
                    )

                    // ── Button 4 – Exit ────────────────────────────────────
                    OverlayButton(
                        icon        = Icons.Default.ExitToApp,
                        label       = "Exit the app",
                        description = "Go back to home screen",
                        tint        = Color(0xFFF44336),
                        onClick     = callbacks.onExit
                    )

                    // ── Tasks section (only if DB has tasks) ──────────────
                    if (tasks.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            text       = "Your tasks",
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.primary
                        )
                        LazyColumn(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(tasks) { task ->
                                Surface(
                                    shape  = RoundedCornerShape(8.dp),
                                    color  = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckBoxOutlineBlank,
                                            contentDescription = null,
                                            tint   = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text  = task.title,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Timer slider dialog ────────────────────────────────────────────────
    if (showTimerPicker) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Choose break duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$chosenMinutes minutes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Slider(
                    value = chosenMinutes.toFloat(),
                    onValueChange = { chosenMinutes = it.toInt() },
                    valueRange = 5f..60f,
                    steps = 54
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("5 min", style = MaterialTheme.typography.bodySmall)
                    Text("60 min", style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    TextButton(
                        onClick = { showTimerPicker = false }
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            callbacks.onCustomTimer(chosenMinutes)
                            showTimerPicker = false
                        }
                    ) {
                        Text("Start break")
                    }
                }
            }
        }
    }
}


// for Reusable button card
@Composable
private fun OverlayButton(
    icon        : ImageVector,
    label       : String,
    description : String,
    tint        : Color,
    onClick     : () -> Unit
) {
    OutlinedCard(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        border   = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
