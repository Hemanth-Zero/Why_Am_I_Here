package com.example.week2

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whyamihere.View.AppBottomBar
import com.example.whyamihere.View.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    Sc1: () -> Unit,
    Sc2: () -> Unit,
    Sc3: () -> Unit
) {

    val checklist = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("To Do List") }
            )
        },
        bottomBar = {
            AppBottomBar(
                Sc1,
                Sc2 = Sc2,
                Sc3 = Sc3,
                selected = Screens.TasksScreen.id
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Adding(list = checklist)

            Divider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(checklist) { item ->

                    CheckCard(
                        name = item,
                        modifier = Modifier.padding(vertical = 4.dp),
                        delete = { checklist.remove(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun Adding(list: MutableList<String>) {

    var name by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.weight(1f),
                label = { Text("New Task") },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        list.add(name)
                        name = ""
                    }
                }
            ) {
                Text("Add")
            }
        }
    }
}

@Composable
fun CheckCard(
    name: String,
    modifier: Modifier,
    delete: () -> Unit
) {

    var clicked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    if (clicked) Icons.Filled.CheckCircle
                    else Icons.Outlined.CheckCircle,

                contentDescription = "Check",

                modifier = Modifier
                    .clickable { clicked = !clicked }
                    .padding(end = 12.dp)
            )

            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                textDecoration =
                    if (clicked)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
            )

            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = "Delete",
                modifier = Modifier
                    .clickable { delete() }
                    .padding(start = 12.dp)
            )
        }
    }
}