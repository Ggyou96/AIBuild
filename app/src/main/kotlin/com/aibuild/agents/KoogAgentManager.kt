package com.aibuild.agents

import android.content.Context
import com.aibuild.BuildConfig
import com.aibuild.agents.core.*
import com.aibuild.monitoring.EventLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * KoogAgentManager - Central orchestrator for the AI agent system
 * 
 * Manages:
 * - Agent routing and lifecycle
 * - Tool execution and state management
 * - Per-agent conversation state
 * - Integration with EventLogger monitoring
 * - Chat interface communication
 * 
 * @author AIBuild Agent System
 */
@Singleton
class KoogAgentManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventLogger: EventLogger
) : ToolExecutor {
    
    companion object {
        private const val SESSION_TIMEOUT_MS = 30 * 60 * 1000L // 30 minutes
        private const val MAX_CONVERSATION_HISTORY = 50
    }
    
    // Agent instances
    private val plannerAgent = PlannerAgent()
    private val teammateAgent = TeammateAgent()
    private val builderAgent = BuilderAgent()
    
    // State management
    private val _currentAgent = MutableStateFlow(AgentType.PLANNER)
    val currentAgent: StateFlow<AgentType> = _currentAgent.asStateFlow()
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()
    
    private val _agentStates = MutableStateFlow(
        mapOf<AgentType, AgentState>(
            AgentType.PLANNER to AgentState(),
            AgentType.TEAMMATE to AgentState(),
            AgentType.BUILDER to AgentState()
        )
    )
    val agentStates: StateFlow<Map<AgentType, AgentState>> = _agentStates.asStateFlow()
    
    // Tool execution state
    private val _pendingUserInput = MutableStateFlow<String?>(null)
    private val _waitingForInput = MutableStateFlow(false)
    private val _lastToolResult = MutableStateFlow<ToolResult?>(null)
    
    // Conversation state
    private val _conversationHistory = MutableStateFlow<Map<AgentType, List<String>>>(
        mapOf(
            AgentType.PLANNER to emptyList(),
            AgentType.TEAMMATE to emptyList(),
            AgentType.BUILDER to emptyList()
        )
    )
    val conversationHistory: StateFlow<Map<AgentType, List<String>>> = _conversationHistory.asStateFlow()
    
    // Chat interface communication
    private val _chatMessages = MutableSharedFlow<ChatMessage>(replay = 100)
    val chatMessages: SharedFlow<ChatMessage> = _chatMessages.asSharedFlow()
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    init {
        // Initialize agents
        scope.launch {
            initializeAgents()
        }
        
        // Start session timeout monitoring
        scope.launch {
            monitorSessionTimeouts()
        }
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("KoogAgentManager", "Agent system initialized")
        }
    }
    
    /**
     * Initialize all agents with their configurations
     */
    private suspend fun initializeAgents() {
        try {
            val loadResults = listOf(
                async { plannerAgent.loadConfiguration(context) },
                async { teammateAgent.loadConfiguration(context) },
                async { builderAgent.loadConfiguration(context) }
            ).awaitAll()
            
            if (loadResults.all { it }) {
                eventLogger.logEvent("agent_system_initialized", mapOf(
                    "agents_loaded" to 3,
                    "success" to true
                ))
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("KoogAgentManager", "All agents initialized successfully")
                }
            } else {
                eventLogger.logEvent("agent_system_init_failed", mapOf(
                    "loaded_count" to loadResults.count { it }
                ))
            }
        } catch (e: Exception) {
            eventLogger.logEvent("agent_system_init_error", mapOf(
                "error" to e.message
            ))
            
            if (BuildConfig.DEBUG) {
                android.util.Log.e("KoogAgentManager", "Failed to initialize agents", e)
            }
        }
    }
    
    /**
     * Switch to a different agent
     */
    suspend fun switchAgent(agentType: AgentType) {
        val previousAgent = _currentAgent.value
        _currentAgent.value = agentType
        
        // Log agent switch
        eventLogger.logEvent("agent_switched", mapOf(
            "from" to previousAgent.displayName,
            "to" to agentType.displayName,
            "timestamp" to System.currentTimeMillis()
        ))
        
        // Send switch notification to chat
        _chatMessages.emit(
            ChatMessage(
                content = "🔄 Switched to **${agentType.displayName} Agent**",
                type = ChatMessage.Type.SYSTEM,
                agentType = agentType,
                timestamp = System.currentTimeMillis()
            )
        )
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("KoogAgentManager", "Switched from ${previousAgent.displayName} to ${agentType.displayName}")
        }
    }
    
    /**
     * Process user input through the current agent
     */
    suspend fun processUserInput(input: String): Result<Unit> {
        if (_isProcessing.value) {
            return Result.failure(IllegalStateException("Already processing input"))
        }
        
        return try {
            _isProcessing.value = true
            
            val currentAgentType = _currentAgent.value
            val agent = getAgent(currentAgentType)
            
            // Create tool context
            val toolContext = ToolContext(
                sessionId = generateSessionId(),
                agentType = currentAgentType.displayName,
                conversationHistory = _conversationHistory.value[currentAgentType] ?: emptyList(),
                projectContext = buildProjectContext()
            )
            
            // Log user input
            eventLogger.logEvent("user_input_received", mapOf(
                "agent" to currentAgentType.displayName,
                "input_length" to input.length,
                "session_id" to toolContext.sessionId
            ))
            
            // Add to conversation history
            addToConversationHistory(currentAgentType, "User: $input")
            
            // Process input through agent
            val response = agent.processInput(input, toolContext, this)
            
            // Update agent state
            updateAgentState(currentAgentType, response.state)
            
            // Execute tools
            executeAgentTools(response.tools, currentAgentType)
            
            // Log successful processing
            eventLogger.logEvent("user_input_processed", mapOf(
                "agent" to currentAgentType.displayName,
                "tools_executed" to response.tools.size,
                "success" to true
            ))
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            eventLogger.logEvent("user_input_error", mapOf(
                "agent" to _currentAgent.value.displayName,
                "error" to e.message
            ))
            
            // Send error message to chat
            _chatMessages.emit(
                ChatMessage(
                    content = "❌ An error occurred while processing your request: ${e.message}",
                    type = ChatMessage.Type.ERROR,
                    agentType = _currentAgent.value,
                    timestamp = System.currentTimeMillis()
                )
            )
            
            Result.failure(e)
        } finally {
            _isProcessing.value = false
        }
    }
    
    /**
     * Provide user input for pending questions
     */
    suspend fun provideUserInput(input: String) {
        if (_waitingForInput.value) {
            _pendingUserInput.value = input
            _waitingForInput.value = false
            
            eventLogger.logEvent("user_input_provided", mapOf(
                "agent" to _currentAgent.value.displayName,
                "response_length" to input.length
            ))
        }
    }
    
    /**
     * Reset current agent state
     */
    fun resetCurrentAgent() {
        val currentAgentType = _currentAgent.value
        val agent = getAgent(currentAgentType)
        agent.reset()
        
        // Clear conversation history
        val updatedHistory = _conversationHistory.value.toMutableMap()
        updatedHistory[currentAgentType] = emptyList()
        _conversationHistory.value = updatedHistory
        
        // Reset tool state
        _pendingUserInput.value = null
        _waitingForInput.value = false
        _lastToolResult.value = null
        
        eventLogger.logEvent("agent_reset", mapOf(
            "agent" to currentAgentType.displayName
        ))
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("KoogAgentManager", "${currentAgentType.displayName} agent reset")
        }
    }
    
    /**
     * Get workflow for current agent
     */
    fun getCurrentAgentWorkflow(): List<WorkflowStep> {
        return getAgent(_currentAgent.value).getWorkflow()
    }
    
    /**
     * Get current agent state
     */
    fun getCurrentAgentState(): AgentState {
        return _agentStates.value[_currentAgent.value] ?: AgentState()
    }
    
    // ToolExecutor implementation
    
    override suspend fun executeTool(tool: AgentTool): ToolResult {
        return try {
            when (tool) {
                is SayToUser -> {
                    _chatMessages.emit(
                        ChatMessage(
                            content = tool.message,
                            type = mapMessageType(tool.messageType),
                            agentType = _currentAgent.value,
                            timestamp = System.currentTimeMillis(),
                            formatting = tool.formatting
                        )
                    )
                    ToolResult.Success(message = "Message sent to user")
                }
                
                is AskUser -> {
                    _waitingForInput.value = true
                    _chatMessages.emit(
                        ChatMessage(
                            content = tool.question,
                            type = ChatMessage.Type.QUESTION,
                            agentType = _currentAgent.value,
                            timestamp = System.currentTimeMillis(),
                            options = tool.options,
                            context = tool.context
                        )
                    )
                    
                    // Wait for user input with timeout
                    val userInput = waitForUserInput()
                    if (userInput != null) {
                        addToConversationHistory(_currentAgent.value, "User: $userInput")
                        ToolResult.UserInput(userInput)
                    } else {
                        ToolResult.Failure(Exception("User input timeout"))
                    }
                }
                
                is ExitTool -> {
                    _chatMessages.emit(
                        ChatMessage(
                            content = tool.message ?: "Agent workflow completed.",
                            type = ChatMessage.Type.INFO,
                            agentType = _currentAgent.value,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    
                    eventLogger.logEvent("agent_exit", mapOf(
                        "agent" to _currentAgent.value.displayName,
                        "reason" to tool.reason.name,
                        "next_action" to tool.nextAction
                    ))
                    
                    ToolResult.Exit
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("KoogAgentManager", "Tool execution failed", e)
            }
            ToolResult.Failure(e)
        }
    }
    
    override fun getPendingUserInput(): StateFlow<String?> = _pendingUserInput.asStateFlow()
    
    override fun clearPendingInput() {
        _pendingUserInput.value = null
        _waitingForInput.value = false
    }
    
    override fun isWaitingForInput(): Boolean = _waitingForInput.value
    
    // Private helper methods
    
    private fun getAgent(agentType: AgentType): Agent {
        return when (agentType) {
            AgentType.PLANNER -> plannerAgent
            AgentType.TEAMMATE -> teammateAgent
            AgentType.BUILDER -> builderAgent
        }
    }
    
    private suspend fun executeAgentTools(tools: List<AgentTool>, agentType: AgentType) {
        for (tool in tools) {
            val result = executeTool(tool)
            _lastToolResult.value = result
            
            // Log tool execution
            eventLogger.logEvent("tool_executed", mapOf(
                "agent" to agentType.displayName,
                "tool_type" to tool.toolType.name,
                "success" to (result is ToolResult.Success || result is ToolResult.UserInput)
            ))
            
            // Handle specific tool results
            when (result) {
                is ToolResult.Exit -> {
                    // Agent workflow completed
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("KoogAgentManager", "Agent ${agentType.displayName} workflow completed")
                    }
                    break
                }
                is ToolResult.Failure -> {
                    if (BuildConfig.DEBUG) {
                        android.util.Log.w("KoogAgentManager", "Tool execution failed: ${result.error.message}")
                    }
                }
                else -> {
                    // Continue with next tool
                }
            }
        }
    }
    
    private fun updateAgentState(agentType: AgentType, newState: AgentState) {
        val currentStates = _agentStates.value.toMutableMap()
        currentStates[agentType] = newState
        _agentStates.value = currentStates
    }
    
    private fun addToConversationHistory(agentType: AgentType, message: String) {
        val currentHistory = _conversationHistory.value.toMutableMap()
        val agentHistory = currentHistory[agentType]?.toMutableList() ?: mutableListOf()
        
        agentHistory.add(message)
        
        // Limit history size
        if (agentHistory.size > MAX_CONVERSATION_HISTORY) {
            agentHistory.removeFirst()
        }
        
        currentHistory[agentType] = agentHistory
        _conversationHistory.value = currentHistory
    }
    
    private suspend fun waitForUserInput(): String? {
        return withTimeoutOrNull(300_000) { // 5 minute timeout
            _pendingUserInput.filterNotNull().first()
        }
    }
    
    private fun generateSessionId(): String {
        return "session_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    private fun buildProjectContext(): Map<String, Any> {
        return mapOf(
            "app_name" to "AIBuild",
            "platform" to "Android",
            "architecture" to "MVVM",
            "ui_framework" to "Jetpack Compose",
            "database" to "Room + SQLCipher",
            "security_level" to "Enterprise",
            "current_agent" to _currentAgent.value.displayName
        )
    }
    
    private fun mapMessageType(agentMessageType: SayToUser.MessageType): ChatMessage.Type {
        return when (agentMessageType) {
            SayToUser.MessageType.INFO -> ChatMessage.Type.INFO
            SayToUser.MessageType.SUCCESS -> ChatMessage.Type.SUCCESS
            SayToUser.MessageType.WARNING -> ChatMessage.Type.WARNING
            SayToUser.MessageType.ERROR -> ChatMessage.Type.ERROR
            SayToUser.MessageType.PROGRESS -> ChatMessage.Type.PROGRESS
            SayToUser.MessageType.THINKING -> ChatMessage.Type.THINKING
            SayToUser.MessageType.RECOMMENDATION -> ChatMessage.Type.RECOMMENDATION
        }
    }
    
    private suspend fun monitorSessionTimeouts() {
        while (true) {
            delay(60_000) // Check every minute
            
            val currentTime = System.currentTimeMillis()
            val states = _agentStates.value
            
            states.forEach { (agentType, state) ->
                if (state.isActive && (currentTime - state.lastActivity) > SESSION_TIMEOUT_MS) {
                    // Session timeout
                    updateAgentState(agentType, state.copy(isActive = false))
                    
                    eventLogger.logEvent("agent_session_timeout", mapOf(
                        "agent" to agentType.displayName,
                        "session_duration" to (currentTime - state.lastActivity)
                    ))
                }
            }
        }
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.cancel()
        
        eventLogger.logEvent("agent_system_cleanup", mapOf(
            "timestamp" to System.currentTimeMillis()
        ))
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("KoogAgentManager", "Agent system cleaned up")
        }
    }
}

/**
 * ChatMessage - Message data for chat interface
 */
data class ChatMessage(
    val content: String,
    val type: Type,
    val agentType: AgentType,
    val timestamp: Long,
    val options: List<String>? = null,
    val context: String? = null,
    val formatting: SayToUser.MessageFormatting? = null
) {
    enum class Type {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        PROGRESS,
        THINKING,
        RECOMMENDATION,
        QUESTION,
        SYSTEM
    }
}