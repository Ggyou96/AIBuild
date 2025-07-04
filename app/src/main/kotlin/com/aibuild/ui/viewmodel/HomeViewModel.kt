package com.aibuild.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aibuild.BuildConfig
import com.aibuild.agents.ChatMessage
import com.aibuild.agents.KoogAgentManager
import com.aibuild.agents.core.AgentState
import com.aibuild.agents.core.AgentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel - ViewModel for HomeScreen with AI Agent Integration
 * 
 * Manages:
 * - Agent state and switching
 * - Chat message flow from KoogAgentManager
 * - User input processing
 * - UI state for agent interactions
 * 
 * @author AIBuild Agent System
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val agentManager: KoogAgentManager
) : ViewModel() {
    
    // UI State data class
    data class HomeUiState(
        val currentAgent: AgentType = AgentType.PLANNER,
        val chatMessages: List<ChatMessage> = emptyList(),
        val isProcessing: Boolean = false,
        val isWaitingForInput: Boolean = false,
        val agentStates: Map<AgentType, AgentState> = emptyMap(),
        val errorMessage: String? = null
    )
    
    // Private state flows
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Chat messages collection
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    
    init {
        observeAgentManager()
        if (BuildConfig.DEBUG) {
            android.util.Log.d("HomeViewModel", "ViewModel initialized with agent system")
        }
    }
    
    /**
     * Observe KoogAgentManager state changes
     */
    private fun observeAgentManager() {
        viewModelScope.launch {
            // Observe current agent
            agentManager.currentAgent.collect { agentType ->
                _uiState.value = _uiState.value.copy(currentAgent = agentType)
            }
        }
        
        viewModelScope.launch {
            // Observe processing state
            agentManager.isProcessing.collect { isProcessing ->
                _uiState.value = _uiState.value.copy(isProcessing = isProcessing)
            }
        }
        
        viewModelScope.launch {
            // Observe agent states
            agentManager.agentStates.collect { agentStates ->
                _uiState.value = _uiState.value.copy(agentStates = agentStates)
            }
        }
        
        viewModelScope.launch {
            // Observe chat messages
            agentManager.chatMessages.collect { message ->
                val currentMessages = _chatMessages.value.toMutableList()
                currentMessages.add(message)
                _chatMessages.value = currentMessages
                
                _uiState.value = _uiState.value.copy(
                    chatMessages = currentMessages,
                    isWaitingForInput = message.type == ChatMessage.Type.QUESTION
                )
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("HomeViewModel", "New chat message: ${message.type}")
                }
            }
        }
    }
    
    /**
     * Switch to a different agent
     */
    fun switchAgent(agentType: AgentType) {
        viewModelScope.launch {
            try {
                agentManager.switchAgent(agentType)
                
                // Update chat messages for new agent
                val agentHistory = agentManager.conversationHistory.value[agentType] ?: emptyList()
                val chatMessages = convertHistoryToChatMessages(agentHistory, agentType)
                _chatMessages.value = chatMessages
                
                _uiState.value = _uiState.value.copy(
                    currentAgent = agentType,
                    chatMessages = chatMessages,
                    errorMessage = null
                )
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("HomeViewModel", "Switched to ${agentType.displayName} agent")
                }
                
            } catch (e: Exception) {
                handleError("Failed to switch agents: ${e.message}")
            }
        }
    }
    
    /**
     * Send message to current agent
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return
        
        viewModelScope.launch {
            try {
                // Add user message to chat immediately for responsive UI
                val userMessage = ChatMessage(
                    content = message,
                    type = ChatMessage.Type.SYSTEM,
                    agentType = _uiState.value.currentAgent,
                    timestamp = System.currentTimeMillis()
                )
                
                val currentMessages = _chatMessages.value.toMutableList()
                currentMessages.add(userMessage)
                _chatMessages.value = currentMessages
                
                _uiState.value = _uiState.value.copy(
                    chatMessages = currentMessages,
                    isWaitingForInput = false,
                    errorMessage = null
                )
                
                // Process through agent manager
                if (_uiState.value.isWaitingForInput) {
                    // This is a response to an agent question
                    agentManager.provideUserInput(message)
                } else {
                    // This is a new conversation input
                    val result = agentManager.processUserInput(message)
                    if (result.isFailure) {
                        handleError("Failed to process message: ${result.exceptionOrNull()?.message}")
                    }
                }
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("HomeViewModel", "Message sent to ${_uiState.value.currentAgent.displayName}: ${message.take(50)}...")
                }
                
            } catch (e: Exception) {
                handleError("Error sending message: ${e.message}")
            }
        }
    }
    
    /**
     * Reset current agent state
     */
    fun resetAgent() {
        viewModelScope.launch {
            try {
                agentManager.resetCurrentAgent()
                
                // Clear chat messages for current agent
                _chatMessages.value = emptyList()
                
                _uiState.value = _uiState.value.copy(
                    chatMessages = emptyList(),
                    isWaitingForInput = false,
                    errorMessage = null
                )
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("HomeViewModel", "${_uiState.value.currentAgent.displayName} agent reset")
                }
                
            } catch (e: Exception) {
                handleError("Failed to reset agent: ${e.message}")
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Get current agent workflow
     */
    fun getCurrentAgentWorkflow() = agentManager.getCurrentAgentWorkflow()
    
    /**
     * Get current agent state
     */
    fun getCurrentAgentState() = agentManager.getCurrentAgentState()
    
    // Private helper methods
    
    /**
     * Convert conversation history to chat messages
     */
    private fun convertHistoryToChatMessages(
        history: List<String>,
        agentType: AgentType
    ): List<ChatMessage> {
        return history.mapIndexed { index, historyItem ->
            val isUserMessage = historyItem.startsWith("User:")
            val content = if (isUserMessage) {
                historyItem.removePrefix("User:").trim()
            } else {
                historyItem.removePrefix("Agent:").trim()
            }
            
            ChatMessage(
                content = content,
                type = if (isUserMessage) ChatMessage.Type.SYSTEM else ChatMessage.Type.INFO,
                agentType = agentType,
                timestamp = System.currentTimeMillis() - (history.size - index) * 1000 // Approximate timestamps
            )
        }
    }
    
    /**
     * Handle errors with user-friendly messages
     */
    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message,
            isProcessing = false
        )
        
        if (BuildConfig.DEBUG) {
            android.util.Log.e("HomeViewModel", message)
        }
    }
    
    /**
     * Clean up resources
     */
    override fun onCleared() {
        super.onCleared()
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("HomeViewModel", "ViewModel cleared")
        }
    }
}