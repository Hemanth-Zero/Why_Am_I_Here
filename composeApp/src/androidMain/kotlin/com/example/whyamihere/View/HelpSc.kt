package com.example.whyamihere.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            HelpCard("How it works") {
                Bullet("Open a tracked app")
                Bullet("See intention prompt")
                Bullet("Select your goal")
                Bullet("Use app mindfully")
            }

            HelpCard("Why am I seeing this?") {
                Text("You enabled tracking for this app to reduce mindless usage.")
            }

            HelpCard("Permissions") {
                Bullet("Usage Access – track usage")
                Bullet("Overlay – show prompts")
                Bullet("Notifications – reminders")
            }

            HelpCard("FAQ") {
                ExpandableFAQ("Skip prompt?", "Yes, but less effective")
                ExpandableFAQ("Does it block apps?", "No")
            }
        }
    }
}

@Composable
fun HelpCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            content()
        }
    }
}

@Composable
fun Bullet(text: String) {
    Text("• $text")
}

@Composable
fun ExpandableFAQ(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        TextButton(onClick = { expanded = !expanded }) {
            Text(question)
        }

        if (expanded) {
            Text(answer, modifier = Modifier.padding(start = 8.dp))
        }
    }
}