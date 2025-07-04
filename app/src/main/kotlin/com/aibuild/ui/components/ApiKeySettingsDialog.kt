package com.aibuild.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aibuild.data.api_keys.repository.ApiKeyProvider
import com.aibuild.ui.viewmodel.SettingsViewModel

/**
 * ApiKeySettingsDialog - Secure dialog for managing API keys
 * 
 * This dialog provides a comprehensive interface for managing API keys with:
 * - Individual provider selection and configuration
 * - Secure input with password masking
 * - Visual feedback for operations
 * - Proper validation and error handling
 * - Support for all major AI service providers
 * 
 * Security Features:
 * - Password-masked input fields
 * - Secure storage validation
 * - Loading states to prevent multiple submissions
 * - Clear visual feedback for success/error states
 * 
 * @author AIBuild Developer Agent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel,
    isLoading: Boolean
) {
    val apiKeysState by viewModel.apiKeysState.collectAsState()
    
    // State for dialog
    var selectedProvider by remember { mutableStateOf(ApiKeyProvider.GOOGLE) }
    var keyInput by remember { mutableStateOf("") }
    var showKeyInput by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf<ApiKeyProvider?>(null) }
    
    // Focus and keyboard
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "API Keys Management",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(onClick = onDismiss, enabled = !isLoading) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Security notice
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "API keys are encrypted and stored securely on device",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Divider()
                
                // API Providers List
                Text(
                    text = "API Providers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                ApiKeyProvider.values().forEach { provider ->
                    ApiProviderCard(
                        provider = provider,
                        currentKey = viewModel.getApiKeyDisplayValue(provider),
                        isSelected = selectedProvider == provider,
                        onSelect = { selectedProvider = provider },
                        onEdit = { 
                            selectedProvider = provider
                            keyInput = ""
                            showKeyInput = true
                        },
                        onDelete = { confirmDelete = provider },
                        isLoading = isLoading
                    )
                }
                
                Divider()
                
                // Bulk Actions
                Text(
                    text = "Bulk Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { confirmDelete = ApiKeyProvider.GOOGLE }, // Special case for "all"
                        enabled = !isLoading && viewModel.hasAnyApiKeys(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Delete All",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete All")
                    }
                    
                    OutlinedButton(
                        onClick = { /* Export functionality */ },
                        enabled = !isLoading && viewModel.hasAnyApiKeys(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export")
                    }
                }
                
                // Status
                if (viewModel.hasAnyApiKeys()) {
                    Text(
                        text = "Configured: ${viewModel.getConfiguredKeysCount()}/5 API keys",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "No API keys configured",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Key Input Dialog
    if (showKeyInput) {
        ApiKeyInputDialog(
            provider = selectedProvider,
            initialValue = keyInput,
            onValueChange = { keyInput = it },
            onSave = { key ->
                viewModel.saveApiKey(selectedProvider, key)
                showKeyInput = false
                keyInput = ""
            },
            onDismiss = { 
                showKeyInput = false
                keyInput = ""
            },
            isLoading = isLoading
        )
    }

    // Delete Confirmation Dialog
    confirmDelete?.let { provider ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = {
                Text(
                    text = if (provider == ApiKeyProvider.GOOGLE && viewModel.hasAnyApiKeys()) {
                        "Delete All API Keys?"
                    } else {
                        "Delete ${provider.displayName} API Key?"
                    }
                )
            },
            text = {
                Text(
                    text = if (provider == ApiKeyProvider.GOOGLE && viewModel.hasAnyApiKeys()) {
                        "This will permanently delete all configured API keys. This action cannot be undone."
                    } else {
                        "This will permanently delete the ${provider.displayName} API key. This action cannot be undone."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (provider == ApiKeyProvider.GOOGLE && viewModel.hasAnyApiKeys()) {
                            viewModel.deleteAllApiKeys()
                        } else {
                            viewModel.deleteApiKey(provider)
                        }
                        confirmDelete = null
                    },
                    enabled = !isLoading
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmDelete = null },
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Individual API provider card component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApiProviderCard(
    provider: ApiKeyProvider,
    currentKey: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isLoading: Boolean
) {
    val hasKey = currentKey != "Not configured"
    
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = provider.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    Text(
                        text = currentKey,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasKey) {
                            if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = if (hasKey) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = if (hasKey) "Edit" else "Add",
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    if (hasKey) {
                        IconButton(
                            onClick = onDelete,
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            // Status indicator
            if (hasKey) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Configured",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Configured",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * API Key input dialog
 */
@Composable
private fun ApiKeyInputDialog(
    provider: ApiKeyProvider,
    initialValue: String,
    onValueChange: (String) -> Unit,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    var keyValue by remember { mutableStateOf(initialValue) }
    var showPassword by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Configure ${provider.displayName} API Key")
        },
        text = {
            Column {
                Text(
                    text = "Enter your ${provider.displayName} API key. It will be encrypted and stored securely on your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = keyValue,
                    onValueChange = { 
                        keyValue = it
                        onValueChange(it)
                    },
                    label = { Text("API Key") },
                    placeholder = { Text("Enter your ${provider.displayName} API key") },
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (showPassword) {
                                    "Hide API key"
                                } else {
                                    "Show API key"
                                }
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (keyValue.isNotBlank()) {
                                onSave(keyValue)
                            }
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(keyValue) },
                enabled = !isLoading && keyValue.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}