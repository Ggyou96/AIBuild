package com.aibuild.pages.Settings.features.BiometricAuth.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*

@Composable
fun BiometricAuthSetting() {
    var isEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Biometric Authentication", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Enable Biometrics")
            Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
        }
    }
}
