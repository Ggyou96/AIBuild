package com.aibuild.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aibuild.ui.theme.AIBuildTheme
import com.aibuild.ui.theme.PlannerAgentColor
import com.aibuild.ui.theme.TeammateAgentColor
import com.aibuild.ui.theme.BuilderAgentColor
import com.aibuild.agents.core.AgentType
import com.aibuild.agents.ChatMessage
import com.aibuild.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * HomeScreen - Main screen of the AIBuild app with integrated AI Agent System
 * 
 * This screen serves as the central hub for users, featuring:
 * - AI Agent Chat Panel with real Koog.ai agents (Planner, Teammate, Builder)
 * - Live Projects section
 * - Top Community Apps showcase  
 * - Project Description section
 * 
 * The screen integrates with KoogAgentManager for actual AI agent interactions.
 * 
 * @author AIBuild Agent System
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
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
        
        // AI Agent Chat Panel Section
        AgentChatPanel(
            currentAgent = uiState.currentAgent,
            chatMessages = uiState.chatMessages,
            isProcessing = uiState.isProcessing,
            isWaitingForInput = uiState.isWaitingForInput,
            onAgentSwitch = viewModel::switchAgent,
            onSendMessage = viewModel::sendMessage,
            onResetAgent = viewModel::resetAgent
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Live Projects Section
        LiveProjectsSection(
            agentStates = uiState.agentStates
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Top Community Apps Section
        TopCommunityAppsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Project Description Section
        ProjectDescriptionSection()
    }
}

/**
 * AI Agent Chat Panel with real agent integration
 * 
 * Provides interface to interact with the three Koog.ai agents:
 * - Planner: Project planning and requirements analysis
 * - Teammate: Code analysis and developer guidance  
 * - Builder: Code implementation and development
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentChatPanel(
    currentAgent: AgentType,
    chatMessages: List<ChatMessage>,
    isProcessing: Boolean,
    isWaitingForInput: Boolean,
    onAgentSwitch: (AgentType) -> Unit,
    onSendMessage: (String) -> Unit,
    onResetAgent: () -> Unit
) {
    var chatMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Header with Reset Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Koog.ai Agents",
                    style = MaterialTheme.typography.titleLarge
                )
                
                IconButton(
                    onClick = onResetAgent,
                    enabled = !isProcessing
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset Agent",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Agent Toggle Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AgentToggleChip(
                    agentType = AgentType.PLANNER,
                    isSelected = currentAgent == AgentType.PLANNER,
                    onClick = { onAgentSwitch(AgentType.PLANNER) },
                    enabled = !isProcessing
                )
                
                AgentToggleChip(
                    agentType = AgentType.TEAMMATE,
                    isSelected = currentAgent == AgentType.TEAMMATE,
                    onClick = { onAgentSwitch(AgentType.TEAMMATE) },
                    enabled = !isProcessing
                )
                
                AgentToggleChip(
                    agentType = AgentType.BUILDER,
                    isSelected = currentAgent == AgentType.BUILDER,
                    onClick = { onAgentSwitch(AgentType.BUILDER) },
                    enabled = !isProcessing
                )
            }
            
            // Agent Description
            AgentDescriptionCard(currentAgent)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chat Messages
            if (chatMessages.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { message ->
                        AgentChatMessageItem(message)
                    }
                }
            } else {
                // Welcome message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = getAgentColor(currentAgent).copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = getWelcomeMessage(currentAgent),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Processing Indicator
            if (isProcessing) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${currentAgent.displayName} is thinking...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                    label = { 
                        Text(
                            if (isWaitingForInput) "Answer required..." 
                            else "Ask ${currentAgent.displayName}..."
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isProcessing,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getAgentColor(currentAgent),
                        focusedLabelColor = getAgentColor(currentAgent)
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilledIconButton(
                    onClick = {
                        if (chatMessage.isNotBlank()) {
                            onSendMessage(chatMessage)
                            chatMessage = ""
                        }
                    },
                    enabled = !isProcessing && chatMessage.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = getAgentColor(currentAgent)
                    )
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

/**
 * Agent Toggle Chip Component
 */
@Composable
fun AgentToggleChip(
    agentType: AgentType,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    FilterChip(
        onClick = onClick,
        label = { Text(agentType.displayName) },
        selected = isSelected,
        enabled = enabled,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = getAgentColor(agentType).copy(alpha = 0.2f),
            selectedLabelColor = getAgentColor(agentType)
        )
    )
}

/**
 * Agent Description Card
 */
@Composable
fun AgentDescriptionCard(agentType: AgentType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getAgentColor(agentType).copy(alpha = 0.05f)
        )
    ) {
        Text(
            text = agentType.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

/**
 * Agent Chat Message Item with proper formatting
 */
@Composable
fun AgentChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = when (message.type) {
            ChatMessage.Type.SYSTEM -> Arrangement.Center
            else -> Arrangement.Start
        }
    ) {
        Card(
            modifier = Modifier.widthIn(max = 320.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (message.type) {
                    ChatMessage.Type.SUCCESS -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    ChatMessage.Type.WARNING -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    ChatMessage.Type.ERROR -> Color(0xFFF44336).copy(alpha = 0.1f)
                    ChatMessage.Type.QUESTION -> getAgentColor(message.agentType).copy(alpha = 0.15f)
                    ChatMessage.Type.SYSTEM -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    else -> getAgentColor(message.agentType).copy(alpha = 0.1f)
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Agent identifier
                if (message.type != ChatMessage.Type.SYSTEM) {
                    Text(
                        text = "${message.agentType.displayName} Agent",
                        style = MaterialTheme.typography.labelMedium,
                        color = getAgentColor(message.agentType),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                // Message content (with basic markdown support)
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Options for questions
                if (message.options != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    message.options.forEach { option ->
                        Text(
                            text = "• $option",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
                
                // Context for questions
                if (message.context != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.context,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Live Projects Section with agent state information
 */
@Composable
fun LiveProjectsSection(
    agentStates: Map<AgentType, com.aibuild.agents.core.AgentState>
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
                text = "Agent Status",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Agent status cards
            AgentType.values().forEach { agentType ->
                val state = agentStates[agentType]
                AgentStatusCard(
                    agentType = agentType,
                    state = state,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Empty state if no active tasks
            val hasActiveTasks = agentStates.values.any { it?.isActive == true }
            if (!hasActiveTasks) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start a conversation with any agent to begin!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Agent Status Card Component
 */
@Composable
fun AgentStatusCard(
    agentType: AgentType,
    state: com.aibuild.agents.core.AgentState?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (state?.isActive == true) {
                getAgentColor(agentType).copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (state?.isActive == true) {
                            getAgentColor(agentType)
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = agentType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = state?.currentTask ?: "Ready",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Step indicator
            if (state?.isActive == true) {
                Text(
                    text = "Step ${state.currentWorkflowStep + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = getAgentColor(agentType)
                )
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

// Helper functions

/**
 * Get agent color based on agent type
 */
@Composable
fun getAgentColor(agentType: AgentType): Color {
    return when (agentType) {
        AgentType.PLANNER -> PlannerAgentColor
        AgentType.TEAMMATE -> TeammateAgentColor
        AgentType.BUILDER -> BuilderAgentColor
    }
}

/**
 * Get welcome message for agent
 */
fun getWelcomeMessage(agentType: AgentType): String {
    return when (agentType) {
        AgentType.PLANNER -> "👋 Hi! I'm your Planner agent. I follow the principle: \"Real only. Illusion forbidden.\" I can help you turn app ideas into structured development plans with evidence-based analysis."
        AgentType.TEAMMATE -> "👋 Hi! I'm your Teammate agent. I specialize in code analysis, clear explanations, and guided learning. I can help you understand your project and improve your development skills."
        AgentType.BUILDER -> "👋 Hi! I'm your Builder agent. My core rule: \"Build only after Plan.md & RoadMap.md exist.\" I transform approved plans into high-quality, production-ready code."
    }
}

/**
 * Data classes for mock data
 */
data class CommunityApp(
    val name: String,
    val stars: Int
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AIBuildTheme {
        // Note: Preview won't show real agent functionality
        // since it requires ViewModel injection
        Surface {
            Text(
                text = "HomeScreen Preview\n(Requires ViewModel for full functionality)",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}