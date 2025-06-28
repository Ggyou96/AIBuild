package com.aibuild.pages.DeveloperAgent.features.AgentConsole.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeveloperAgentScreen() {
    var command by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("Awaiting command...") }
    val agentStatus = "Active"

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Developer Agent") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = command,
                onValueChange = { command = it },
                label = { Text("Enter command") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { output = "Executed: $command" },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Run")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Output:", style = MaterialTheme.typography.labelLarge)
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 4.dp)
            ) {
                Box(Modifier.padding(8.dp)) {
                    Text(output)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Agent Status: $agentStatus", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
