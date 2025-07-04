package com.aibuild.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aibuild.ui.theme.AIBuildTheme
import com.aibuild.ui.theme.PlannerAgentColor
import com.aibuild.ui.theme.TeammateAgentColor

/**
 * HomeScreen - Main screen of the AIBuild app
 * 
 * This screen serves as the central hub for users, featuring:
 * - AI Agent Chat Panel with toggle between Planner and Teammate agents
 * - Live Projects section (currently empty state)
 * - Top Community Apps showcase
 * - Project Description section with scrollable content
 * 
 * The design follows Material Design 3 principles and provides
 * a clean, professional interface for mobile development.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "AIBuild",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "World's First Mobile-First AI Development Platform",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Chat Panel Section
        ChatPanelSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Live Projects Section
        LiveProjectsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Top Community Apps Section
        TopCommunityAppsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Project Description Section
        ProjectDescriptionSection()
    }
}

/**
 * Chat Panel Section with Agent Toggle
 * 
 * Provides an interface to interact with AI agents (Planner and Teammate)
 * with a toggle button to switch between agents and a simple chat interface.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPanelSection() {
    var selectedAgent by remember { mutableStateOf("Planner") }
    var chatMessage by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<ChatMessage>()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Header
            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Agent Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { selectedAgent = "Planner" },
                    label = { Text("Planner") },
                    selected = selectedAgent == "Planner",
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PlannerAgentColor.copy(alpha = 0.2f),
                        selectedLabelColor = PlannerAgentColor
                    )
                )
                
                FilterChip(
                    onClick = { selectedAgent = "Teammate" },
                    label = { Text("Teammate") },
                    selected = selectedAgent == "Teammate",
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TeammateAgentColor.copy(alpha = 0.2f),
                        selectedLabelColor = TeammateAgentColor
                    )
                )
            }
            
            // Chat History (Mock)
            if (chatHistory.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatHistory) { message ->
                        ChatMessageItem(message)
                    }
                }
            } else {
                // Welcome message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "👋 Hi! I'm your $selectedAgent agent. " +
                                if (selectedAgent == "Planner") {
                                    "I can help you plan your app ideas, create PRDs, and analyze market feasibility."
                                } else {
                                    "I can analyze your code, explain concepts, and guide you through development."
                                },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Chat Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatMessage,
                    onValueChange = { chatMessage = it },
                    label = { Text("Ask $selectedAgent...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilledIconButton(
                    onClick = {
                        if (chatMessage.isNotBlank()) {
                            // Add user message
                            chatHistory = chatHistory + ChatMessage(
                                text = chatMessage,
                                isFromUser = true,
                                agent = selectedAgent
                            )
                            
                            // Add mock agent response
                            val response = getMockAgentResponse(selectedAgent, chatMessage)
                            chatHistory = chatHistory + ChatMessage(
                                text = response,
                                isFromUser = false,
                                agent = selectedAgent
                            )
                            
                            chatMessage = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

/**
 * Live Projects Section
 * 
 * Shows current projects (empty state for now)
 */
@Composable
fun LiveProjectsSection() {
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
                text = "Live Projects",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📱",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "No active projects",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Start a conversation with Planner to begin!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Top Community Apps Section
 * 
 * Showcases popular community-built apps with dummy data
 */
@Composable
fun TopCommunityAppsSection() {
    Column {
        Text(
            text = "Top Community Apps",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getMockCommunityApps()) { app ->
                CommunityAppCard(app)
            }
        }
    }
}

/**
 * Project Description Section
 * 
 * Multi-line scrollable text describing the project or platform
 */
@Composable
fun ProjectDescriptionSection() {
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
                text = "About AIBuild Platform",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = """
                    AIBuild is the world's first mobile-first AI-native development platform powered by Koog.ai Agents. 
                    
                    Our platform revolutionizes Android app development by enabling developers to plan, build, and deploy applications directly on their mobile devices using intelligent AI agents.
                    
                    Key Features:
                    
                    🤖 AI Agent Collaboration
                    Work with specialized agents - Planner for project planning, Teammate for code analysis, and Builder for implementation.
                    
                    📱 Mobile-First Development
                    Complete development workflow optimized for mobile devices with touch-friendly interfaces and gesture controls.
                    
                    🔧 Integrated Development Environment
                    Full-featured IDE with file management, monitoring & diagnostics, and agent workspace - all in your pocket.
                    
                    🚀 Intelligent Automation
                    From idea validation to deployment, our agents help automate repetitive tasks while maintaining code quality.
                    
                    Built for the next generation of developers who want to code anywhere, anytime, with the power of AI assistance.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
            )
        }
    }
}

/**
 * Individual community app card component
 */
@Composable
fun CommunityAppCard(app: CommunityApp) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = app.stars.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Chat message item component
 */
@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    when (message.agent) {
                        "Planner" -> PlannerAgentColor.copy(alpha = 0.2f)
                        "Teammate" -> TeammateAgentColor.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                }
            )
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isFromUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

/**
 * Data classes for mock data
 */
data class CommunityApp(
    val name: String,
    val stars: Int
)

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val agent: String
)

/**
 * Mock data generators
 */
fun getMockCommunityApps(): List<CommunityApp> {
    return listOf(
        CommunityApp("Weather Pro", 1250),
        CommunityApp("Task Manager", 980),
        CommunityApp("Fitness Tracker", 2100),
        CommunityApp("Recipe Book", 750),
        CommunityApp("Photo Editor", 1800)
    )
}

fun getMockAgentResponse(agent: String, userMessage: String): String {
    return when (agent) {
        "Planner" -> when {
            userMessage.lowercase().contains("app") -> 
                "I'd be happy to help you plan your app! Let me analyze the concept and create a comprehensive PRD. What's your app idea about?"
            userMessage.lowercase().contains("market") -> 
                "Great question! I can perform market research and competitive analysis. Let me gather data on market viability and user demand."
            else -> 
                "As your Planner agent, I can help you validate ideas, create PRDs, and develop comprehensive project plans. What would you like to work on?"
        }
        "Teammate" -> when {
            userMessage.lowercase().contains("code") -> 
                "I can analyze your code structure and provide detailed explanations. Which file or concept would you like me to explain?"
            userMessage.lowercase().contains("error") -> 
                "I'll help you debug that issue! Can you share the error message or describe what's not working as expected?"
            else -> 
                "As your Teammate agent, I'm here to help you understand your codebase and guide you through development challenges. What can I explain for you?"
        }
        else -> "Hello! How can I assist you today?"
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AIBuildTheme {
        HomeScreen()
    }
}