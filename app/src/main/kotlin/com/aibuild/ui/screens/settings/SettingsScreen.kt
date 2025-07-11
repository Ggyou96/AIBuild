package com.aibuild.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aibuild.ui.theme.AIBuildTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aibuild.data.api_keys.repository.ApiKeyProvider
import com.aibuild.ui.components.ApiKeySettingsDialog
import com.aibuild.ui.theme.AIBuildTheme
import com.aibuild.ui.viewmodel.SettingsViewModel

/**
 * Settings Screen - Configuration and preferences for the AIBuild app
 * 
 * This screen provides access to:
 * - App settings and preferences
 * - Developer options and configurations
 * - Developer options and configurations (including secure API key management)
 * - Agent configuration and behavior settings
 * - Account and profile management
 * - About and help information
 * 
 * Currently implemented with placeholder UI and mock settings.
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Configure your AIBuild experience",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // App Settings Section
        AppSettingsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Developer Options Section
        DeveloperOptionsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Agent Configuration Section
        AgentConfigurationSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Account & Profile Section
        AccountProfileSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // About & Help Section
        AboutHelpSection()
 * Features secure API key management with:
 * - Developer mode enforcement for access
 * - Encrypted storage using Room + SQLCipher
 * - Visual feedback for all operations
 * - Proper error handling and security measures
 * 
 * @author AIBuild Developer Agent
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Collect all state from ViewModel
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val autoSaveEnabled by viewModel.autoSaveEnabled.collectAsState()
    val debugModeEnabled by viewModel.debugModeEnabled.collectAsState()
    val showPerformanceOverlay by viewModel.showPerformanceOverlay.collectAsState()
    val enableBetaFeatures by viewModel.enableBetaFeatures.collectAsState()
    val verboseResponses by viewModel.verboseResponses.collectAsState()
    val autoSuggestionsEnabled by viewModel.autoSuggestionsEnabled.collectAsState()
    val multiAgentMode by viewModel.multiAgentMode.collectAsState()
    
    val showApiKeyDialog by viewModel.showApiKeyDialog.collectAsState()
    val apiKeysError by viewModel.apiKeysError.collectAsState()
    val apiKeysSuccess by viewModel.apiKeysSuccess.collectAsState()
    val isApiKeysLoading by viewModel.isApiKeysLoading.collectAsState()
    
    // Snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle error messages
    LaunchedEffect(apiKeysError) {
        apiKeysError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Dismiss"
            )
            viewModel.clearMessages()
        }
    }
    
    // Handle success messages
    LaunchedEffect(apiKeysSuccess) {
        apiKeysSuccess?.let { success ->
            snackbarHostState.showSnackbar(
                message = success,
                actionLabel = "OK"
            )
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Configure your AIBuild experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // App Settings Section
            AppSettingsSection(
                darkModeEnabled = darkModeEnabled,
                notificationsEnabled = notificationsEnabled,
                autoSaveEnabled = autoSaveEnabled,
                onDarkModeToggle = viewModel::toggleDarkMode,
                onNotificationsToggle = viewModel::toggleNotifications,
                onAutoSaveToggle = viewModel::toggleAutoSave
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Developer Options Section
            DeveloperOptionsSection(
                debugModeEnabled = debugModeEnabled,
                showPerformanceOverlay = showPerformanceOverlay,
                enableBetaFeatures = enableBetaFeatures,
                onDebugModeToggle = viewModel::toggleDebugMode,
                onPerformanceOverlayToggle = viewModel::togglePerformanceOverlay,
                onBetaFeaturesToggle = viewModel::toggleBetaFeatures,
                onApiKeysClick = { 
                    if (debugModeEnabled) {
                        viewModel.showApiKeyDialog(ApiKeyProvider.GOOGLE)
                    }
                },
                configuredKeysCount = viewModel.getConfiguredKeysCount(),
                isApiKeysLoading = isApiKeysLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Agent Configuration Section
            AgentConfigurationSection(
                verboseResponses = verboseResponses,
                autoSuggestionsEnabled = autoSuggestionsEnabled,
                multiAgentMode = multiAgentMode,
                onVerboseResponsesToggle = viewModel::toggleVerboseResponses,
                onAutoSuggestionsToggle = viewModel::toggleAutoSuggestions,
                onMultiAgentModeToggle = viewModel::toggleMultiAgentMode
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account & Profile Section
            AccountProfileSection()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // About & Help Section
            AboutHelpSection()
        }
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // API Key Management Dialog
    showApiKeyDialog?.let { provider ->
        ApiKeySettingsDialog(
            onDismiss = { viewModel.hideApiKeyDialog() },
            viewModel = viewModel,
            isLoading = isApiKeysLoading
        )
    }
}

/**
 * App settings and general preferences
 */
@Composable
private fun AppSettingsSection() {
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoSaveEnabled by remember { mutableStateOf(true) }
    
private fun AppSettingsSection(
    darkModeEnabled: Boolean,
    notificationsEnabled: Boolean,
    autoSaveEnabled: Boolean,
    onDarkModeToggle: () -> Unit,
    onNotificationsToggle: () -> Unit,
    onAutoSaveToggle: () -> Unit
) {
    SettingsCard(
        title = "⚙️ App Settings",
        description = "General app preferences and behavior"
    ) {
        // Dark Mode Toggle
        SettingToggleItem(
            icon = Icons.Default.DarkMode,
            title = "Dark Mode",
            description = "Use dark theme throughout the app",
            checked = darkModeEnabled,
            onCheckedChange = { darkModeEnabled = it }
            onCheckedChange = { onDarkModeToggle() }
        )
        
        // Notifications Toggle
        SettingToggleItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            description = "Receive build and agent notifications",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
            onCheckedChange = { onNotificationsToggle() }
        )
        
        // Auto-save Toggle
        SettingToggleItem(
            icon = Icons.Default.Save,
            title = "Auto-save",
            description = "Automatically save changes while editing",
            checked = autoSaveEnabled,
            onCheckedChange = { autoSaveEnabled = it }
            onCheckedChange = { onAutoSaveToggle() }
        )
        
        // Language Selection
        SettingClickableItem(
            icon = Icons.Default.Language,
            title = "Language",
            description = "English (US)",
            onClick = { /* Handle language selection */ }
        )
    }
}

/**
 * Developer-specific options and configurations
 */
@Composable
private fun DeveloperOptionsSection() {
    var debugModeEnabled by remember { mutableStateOf(false) }
    var showPerformanceOverlay by remember { mutableStateOf(false) }
    var enableBetaFeatures by remember { mutableStateOf(false) }
    
private fun DeveloperOptionsSection(
    debugModeEnabled: Boolean,
    showPerformanceOverlay: Boolean,
    enableBetaFeatures: Boolean,
    onDebugModeToggle: () -> Unit,
    onPerformanceOverlayToggle: () -> Unit,
    onBetaFeaturesToggle: () -> Unit,
    onApiKeysClick: () -> Unit,
    configuredKeysCount: Int,
    isApiKeysLoading: Boolean
) {
    SettingsCard(
        title = "🔧 Developer Options",
        description = "Advanced settings for development and debugging"
    ) {
        // Debug Mode Toggle
        SettingToggleItem(
            icon = Icons.Default.BugReport,
            title = "Debug Mode",
            description = "Enable detailed logging and debug information",
            checked = debugModeEnabled,
            onCheckedChange = { debugModeEnabled = it }
            description = "Enable detailed logging and secure API key storage",
            checked = debugModeEnabled,
            onCheckedChange = { onDebugModeToggle() }
        )
        
        // Performance Overlay Toggle
        SettingToggleItem(
            icon = Icons.Default.Speed,
            title = "Performance Overlay",
            description = "Show real-time performance metrics on screen",
            checked = showPerformanceOverlay,
            onCheckedChange = { showPerformanceOverlay = it }
            onCheckedChange = { onPerformanceOverlayToggle() }
        )
        
        // Beta Features Toggle
        SettingToggleItem(
            icon = Icons.Default.Science,
            title = "Beta Features",
            description = "Enable experimental features and tools",
            checked = enableBetaFeatures,
            onCheckedChange = { enableBetaFeatures = it }
            onCheckedChange = { onBetaFeaturesToggle() }
        )
        
        // Build Configuration
        SettingClickableItem(
            icon = Icons.Default.Build,
            title = "Build Configuration",
            description = "Configure Gradle and build settings",
            onClick = { /* Handle build config */ }
        )
        
        // API Keys Management
        SettingClickableItem(
            icon = Icons.Default.Key,
            title = "API Keys",
            description = "Manage external service API keys",
            onClick = { /* Handle API keys */ }
            description = if (debugModeEnabled) {
                "Configured: $configuredKeysCount/5 keys ${if (isApiKeysLoading) "(Loading...)" else ""}"
            } else {
                "Requires Developer Mode to access"
            },
            onClick = onApiKeysClick,
            enabled = debugModeEnabled && !isApiKeysLoading
        )
    }
}

/**
 * AI agent configuration and behavior settings
 */
@Composable
private fun AgentConfigurationSection() {
    var verboseResponses by remember { mutableStateOf(false) }
    var autoSuggestionsEnabled by remember { mutableStateOf(true) }
    var multiAgentMode by remember { mutableStateOf(false) }
    
private fun AgentConfigurationSection(
    verboseResponses: Boolean,
    autoSuggestionsEnabled: Boolean,
    multiAgentMode: Boolean,
    onVerboseResponsesToggle: () -> Unit,
    onAutoSuggestionsToggle: () -> Unit,
    onMultiAgentModeToggle: () -> Unit
) {
    SettingsCard(
        title = "🤖 Agent Configuration",
        description = "Customize AI agent behavior and interactions"
    ) {
        // Verbose Responses Toggle
        SettingToggleItem(
            icon = Icons.Default.Chat,
            title = "Verbose Responses",
            description = "Get detailed explanations from agents",
            checked = verboseResponses,
            onCheckedChange = { verboseResponses = it }
            onCheckedChange = { onVerboseResponsesToggle() }
        )
        
        // Auto Suggestions Toggle
        SettingToggleItem(
            icon = Icons.Default.AutoAwesome,
            title = "Auto Suggestions",
            description = "Receive proactive agent suggestions",
            checked = autoSuggestionsEnabled,
            onCheckedChange = { autoSuggestionsEnabled = it }
            onCheckedChange = { onAutoSuggestionsToggle() }
        )
        
        // Multi-Agent Mode Toggle
        SettingToggleItem(
            icon = Icons.Default.Group,
            title = "Multi-Agent Mode",
            description = "Allow multiple agents to collaborate simultaneously",
            checked = multiAgentMode,
            onCheckedChange = { multiAgentMode = it }
            onCheckedChange = { onMultiAgentModeToggle() }
        )
        
        // Agent Expertise Level
        SettingClickableItem(
            icon = Icons.Default.School,
            title = "Expertise Level",
            description = "Intermediate",
            onClick = { /* Handle expertise level selection */ }
        )
        
        // Agent Memory Settings
        SettingClickableItem(
            icon = Icons.Default.Memory,
            title = "Agent Memory",
            description = "Configure conversation context and history",
            onClick = { /* Handle memory settings */ }
        )
    }
}

/**
 * Account and profile management
 */
@Composable
private fun AccountProfileSection() {
    SettingsCard(
        title = "👤 Account & Profile",
        description = "Manage your account and personal information"
    ) {
        // Profile Information
        SettingClickableItem(
            icon = Icons.Default.Person,
            title = "Profile",
            description = "Edit your profile information",
            onClick = { /* Handle profile edit */ }
        )
        
        // Sync Settings
        SettingClickableItem(
            icon = Icons.Default.CloudSync,
            title = "Sync",
            description = "Sync settings across devices",
            onClick = { /* Handle sync settings */ }
        )
        
        // Privacy Settings
        SettingClickableItem(
            icon = Icons.Default.PrivacyTip,
            title = "Privacy",
            description = "Manage data collection and privacy preferences",
            onClick = { /* Handle privacy settings */ }
        )
        
        // Backup & Restore
        SettingClickableItem(
            icon = Icons.Default.Backup,
            title = "Backup & Restore",
            description = "Backup projects and restore from cloud",
            onClick = { /* Handle backup settings */ }
        )
    }
}

/**
 * About and help information
 */
@Composable
private fun AboutHelpSection() {
    SettingsCard(
        title = "ℹ️ About & Help",
        description = "App information and support resources"
    ) {
        // Help & Documentation
        SettingClickableItem(
            icon = Icons.Default.Help,
            title = "Help & Documentation",
            description = "User guides and tutorials",
            onClick = { /* Handle help */ }
        )
        
        // Feedback
        SettingClickableItem(
            icon = Icons.Default.Feedback,
            title = "Send Feedback",
            description = "Report issues or suggest improvements",
            onClick = { /* Handle feedback */ }
        )
        
        // About AIBuild
        SettingClickableItem(
            icon = Icons.Default.Info,
            title = "About AIBuild",
            description = "Version 1.0.0 - Build 001",
            onClick = { /* Handle about */ }
        )
        
        // License & Legal
        SettingClickableItem(
            icon = Icons.Default.Gavel,
            title = "License & Legal",
            description = "Open source licenses and legal information",
            onClick = { /* Handle legal */ }
        )
    }
}

/**
 * Settings card wrapper component
 */
@Composable
private fun SettingsCard(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            content()
        }
    }
}

/**
 * Toggle setting item component
 */
@Composable
private fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

/**
 * Clickable setting item component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingClickableItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        ),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    AIBuildTheme {
        SettingsScreen()
        // For preview, we'll show the UI without ViewModel
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Configure your AIBuild experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            AppSettingsSection(
                darkModeEnabled = false,
                notificationsEnabled = true,
                autoSaveEnabled = true,
                onDarkModeToggle = {},
                onNotificationsToggle = {},
                onAutoSaveToggle = {}
            )
        }
    }
}
