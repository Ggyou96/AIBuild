package com.aibuild.agents.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * AgentTool - Base interface for all agent tools
 * 
 * Tools are the primary way agents interact with users and the system.
 * Each tool represents a specific action an agent can perform.
 * 
 * @author AIBuild Agent System
 */
sealed interface AgentTool {
    val toolType: ToolType
    
    enum class ToolType {
        ASK_USER,
        SAY_TO_USER,
        EXIT_TOOL
    }
}

/**
 * AskUser Tool - Prompts user with a question and waits for reply
 * 
 * This tool allows agents to ask users questions and receive responses.
 * The agent workflow will pause until the user provides an answer.
 */
data class AskUser(
    val question: String,
    val context: String? = null,
    val options: List<String>? = null,
    val isRequired: Boolean = true
) : AgentTool {
    override val toolType = AgentTool.ToolType.ASK_USER
}

/**
 * SayToUser Tool - Output a message to the user
 * 
 * This tool allows agents to send messages, information, or responses
 * to the user through the chat interface.
 */
data class SayToUser(
    val message: String,
    val messageType: MessageType = MessageType.INFO,
    val formatting: MessageFormatting? = null
) : AgentTool {
    override val toolType = AgentTool.ToolType.SAY_TO_USER
    
    enum class MessageType {
        INFO,           // General information
        SUCCESS,        // Success confirmation
        WARNING,        // Warning message
        ERROR,          // Error message
        PROGRESS,       // Progress update
        THINKING,       // Agent thinking/processing
        RECOMMENDATION  // Recommendation or suggestion
    }
    
    data class MessageFormatting(
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val hasCodeBlock: Boolean = false,
        val codeLanguage: String? = null,
        val hasMarkdown: Boolean = false
    )
}

/**
 * ExitTool - End current tool or conversation flow
 * 
 * This tool allows agents to gracefully exit from a conversation
 * or complete their current task.
 */
data class ExitTool(
    val reason: ExitReason,
    val message: String? = null,
    val nextAction: String? = null
) : AgentTool {
    override val toolType = AgentTool.ToolType.EXIT_TOOL
    
    enum class ExitReason {
        TASK_COMPLETE,      // Task successfully completed
        USER_REQUEST,       // User requested to exit
        ERROR_OCCURRED,     // Error requiring exit
        INSUFFICIENT_INFO,  // Not enough information to continue
        PREREQUISITES_MISSING, // Required prerequisites not met
        TIMEOUT             // Operation timed out
    }
}

/**
 * ToolResult - Result of executing an agent tool
 * 
 * Encapsulates the outcome of tool execution, including success/failure
 * status and any resulting data or error information.
 */
sealed class ToolResult {
    data class Success(
        val data: Any? = null,
        val message: String? = null
    ) : ToolResult()
    
    data class Failure(
        val error: Throwable,
        val message: String? = null
    ) : ToolResult()
    
    data class UserInput(
        val input: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : ToolResult()
    
    object Pending : ToolResult()
    object Exit : ToolResult()
}

/**
 * ToolExecutor - Interface for executing agent tools
 * 
 * Defines the contract for tool execution within the agent system.
 * Implementations handle the actual execution of tools and provide
 * results back to the agents.
 */
interface ToolExecutor {
    suspend fun executeTool(tool: AgentTool): ToolResult
    
    fun getPendingUserInput(): StateFlow<String?>
    fun clearPendingInput()
    fun isWaitingForInput(): Boolean
}

/**
 * ToolContext - Context information for tool execution
 * 
 * Provides agents with context about the current session, user,
 * and environment for making informed tool execution decisions.
 */
data class ToolContext(
    val userId: String? = null,
    val sessionId: String,
    val agentType: String,
    val conversationHistory: List<String> = emptyList(),
    val projectContext: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)