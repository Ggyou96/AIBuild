package com.aibuild.pages.Settings.features.Language.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun LanguageSetting() {
    val languages = listOf("English", "Arabic")
    var selectedLanguage by remember { mutableStateOf(languages[0]) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Language", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        DropdownMenuBox(
            items = languages,
            selected = selectedLanguage,
            onSelect = { selectedLanguage = it }
        )
    }
}

@Composable
fun DropdownMenuBox(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text("Language") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    onSelect(it)
                    expanded = false
                })
            }
        }
    }
}
