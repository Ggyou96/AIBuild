package com.aibuild.ui.screens.idevspace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aibuild.ui.theme.AIBuildTheme

/**
 * IDevSpaceScreen - Integrated Development Environment Space
 * 
 * This screen serves as the main hub for development tools and features.
 * It provides access to three core development modules:
 * 
 * 1. Files Manager - Complete file management and code editing
 * 2. Monitoring & Diagnostics - Performance monitoring and debugging
 * 3. Agents Workspace - AI agent collaboration environment
 * 
 * Each module is accessible through dedicated navigation buttons with
 * clear descriptions of their functionality.
 */
@Composable
fun IDevSpaceScreen(
    onNavigateToFilesManager: () -> Unit = {},
    onNavigateToMonitoring: () -> Unit = {},
    onNavigateToAgentsWorkspace: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section
        HeaderSection()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Navigation Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Files Manager Card
            IDevFeatureCard(
                title = "Files Manager",
                description = "Complete file management system with code editor, syntax highlighting, and project organization tools.",
                icon = Icons.Default.Folder,
                features = listOf(
                    "Project tree navigation",
                    "Code editor with syntax highlighting", 
                    "File search and replace",
                    "Version control integration"
                ),
                onClick = onNavigateToFilesManager
            )
            
            // Monitoring & Diagnostics Card
            IDevFeatureCard(
                title = "Monitoring & Diagnostics",
                description = "Real-time performance monitoring, crash analysis, and comprehensive debugging tools.",
                icon = Icons.Default.Monitor,
                features = listOf(
                    "Performance metrics tracking",
                    "Crash report analysis",
                    "Memory and CPU profiling",
                    "Network request monitoring"
                ),
                onClick = onNavigateToMonitoring
            )
            
            // Agents Workspace Card
            IDevFeatureCard(
                title = "Agents Workspace",
                description = "Collaborative environment for working with AI agents on code analysis, planning, and implementation.",
                icon = Icons.Default.SmartToy,
                features = listOf(
                    "Multi-agent collaboration",
                    "Code analysis and review",
                    "Project planning assistance",
                    "Automated code generation"
                ),
                onClick = onNavigateToAgentsWorkspace
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Additional Information
        InfoSection()
    }
}

/**
 * Header section with title and description
 */
@Composable
private fun HeaderSection() {
    Column {
        Text(
            text = "IDev Space",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Integrated Development Environment",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Access professional development tools optimized for mobile development. " +
                    "Manage files, monitor performance, and collaborate with AI agents - all from your mobile device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
        )
    }
}

/**
 * Feature card component for each IDev tool
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IDevFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    features: List<String>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Features list
            Text(
                text = "Key Features:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = onClick
                ) {
                    Text("Open $title")
                }
            }
        }
    }
}

/**
 * Additional information section
 */
@Composable
private fun InfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pro Tip",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Start with the Agents Workspace to get help planning your project, " +
                        "then use Files Manager to implement your code, and monitor progress " +
                        "with the Monitoring & Diagnostics tools.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IDevSpaceScreenPreview() {
    AIBuildTheme {
        IDevSpaceScreen()
    }
}