package com.example.whyamihere.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.Model.Task
import com.example.whyamihere.Model.TaskDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    Sc1: () -> Unit,
    Sc2: () -> Unit,
    Sc3: () -> Unit
) {
    val context = LocalContext.current
    val db      = remember { TaskDatabase.getInstance(context) }


    var tasks by remember { mutableStateOf(db.getAllTasks()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("To Do List") })
        },
        bottomBar = {
            AppBottomBar(Sc1, Sc2 = Sc2, Sc3 = Sc3, selected = Screens.TasksScreen.id)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            TaskAdder { title ->
                val newTask = db.insertTask(title)
                tasks = tasks + newTask
            }

            HorizontalDivider()


            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "No tasks yet.\nAdd one above!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task   = task,
                            onDelete = {
                                db.deleteTask(task.id)
                                tasks = tasks.filter { it.id != task.id }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskAdder(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = text,
                onValueChange = { text = it },
                modifier      = Modifier.weight(1f),
                label         = { Text("New Task") },
                singleLine    = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onAdd(text.trim())
                        text = ""
                    }
                }
            ) { Text("Add") }
        }
    }
}


@Composable
private fun TaskCard(task: Task, onDelete: () -> Unit) {
    var checked by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = if (checked) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "Toggle",
                modifier           = Modifier
                    .clickable { checked = !checked }
                    .padding(end = 12.dp),
                tint               = if (checked) MaterialTheme.colorScheme.primary
                                     else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text           = task.title,
                modifier       = Modifier.weight(1f),
                fontSize       = 18.sp,
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
            )
            Icon(
                imageVector        = Icons.Outlined.Clear,
                contentDescription = "Delete",
                modifier           = Modifier
                    .clickable { onDelete() }
                    .padding(start = 12.dp),
                tint               = MaterialTheme.colorScheme.error
            )
        }
    }
}
