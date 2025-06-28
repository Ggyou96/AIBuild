package com.aibuild.pages.Settings.features.APIKeys.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApiKeysSetting() {
    val services = listOf("Google", "OpenAI", "Anthropic", "OpenRouter", "Ollama")
    val keys = remember { mutableStateMapOf<String, String>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("API Keys Manager", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        services.forEach { service ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(service, style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = keys[service] ?: "",
                onValueChange = { keys[service] = it },
                label = { Text("Enter $service API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /* Save key logic */ }) {
                Text("Save Key")
            }
        }
    }
}
