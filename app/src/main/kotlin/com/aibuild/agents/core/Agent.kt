package com.aibuild.agents.core

import android.content.Context
import com.aibuild.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

/**
 * Agent - Base interface for all Koog.ai agents
 * 
 * Defines the core contract that all agents must implement.
 * Each agent has a specific role, rules, and workflow as defined
 * in their respective markdown configuration files.
 * 
 * @author AIBuild Agent System
 */
interface Agent {
    val agentType: AgentType
    val name: String
    val role: String
    val specialization: String
    
    /**
     * Process user input and generate appropriate response
     */
    suspend fun processInput(
        input: String,
        context: ToolContext,
        toolExecutor: ToolExecutor
    ): AgentResponse
    
    /**
     * Load agent configuration from markdown file
     */
    suspend fun loadConfiguration(context: Context): Boolean
    
    /**
     * Get agent's current state
     */
    fun getState(): AgentState
    
    /**
     * Reset agent to initial state
     */
    fun reset()
    
    /**
     * Get agent's workflow steps
     */
    fun getWorkflow(): List<WorkflowStep>
}

/**
 * AgentType - Enumeration of available agent types
 */
enum class AgentType(
    val displayName: String,
    val configFile: String,
    val description: String
) {
    PLANNER(
        displayName = "Planner",
        configFile = "planner_agent.md",
        description = "Project Planning and Requirements Analysis"
    ),
    TEAMMATE(
        displayName = "Teammate", 
        configFile = "teammate_agent.md",
        description = "Project Analysis and Developer Guidance"
    ),
    BUILDER(
        displayName = "Builder",
        configFile = "builder_agent.md", 
        description = "Code Implementation and Development"
    )
}

/**
 * AgentState - Current state of an agent
 */
data class AgentState(
    val isActive: Boolean = false,
    val currentWorkflowStep: Int = 0,
    val conversationHistory: List<String> = emptyList(),
    val context: Map<String, Any> = emptyMap(),
    val lastActivity: Long = System.currentTimeMillis(),
    val isWaitingForInput: Boolean = false,
    val currentTask: String? = null
)

/**
 * AgentResponse - Response from an agent after processing input
 */
data class AgentResponse(
    val tools: List<AgentTool>,
    val state: AgentState,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * WorkflowStep - A step in an agent's workflow
 */
data class WorkflowStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val inputs: List<String> = emptyList(),
    val outputs: List<String> = emptyList(),
    val process: String,
    val isCompleted: Boolean = false
)

/**
 * AgentRule - A rule extracted from agent markdown configuration
 */
data class AgentRule(
    val category: String,
    val rule: String,
    val priority: Int = 0
)

/**
 * AbstractAgent - Base implementation for all agents
 * 
 * Provides common functionality including:
 * - Configuration loading from markdown files
 * - State management
 * - Rule parsing and enforcement
 * - Workflow management
 */
abstract class AbstractAgent(
    override val agentType: AgentType
) : Agent {
    
    protected var configuration: AgentConfiguration? = null
    
    private val _state = MutableStateFlow(AgentState())
    protected val state: StateFlow<AgentState> = _state.asStateFlow()
    
    protected var rules: List<AgentRule> = emptyList()
    protected var workflow: List<WorkflowStep> = emptyList()
    
    override val name: String get() = agentType.displayName
    override val role: String get() = configuration?.role ?: agentType.description
    override val specialization: String get() = configuration?.specialization ?: ""
    
    override suspend fun loadConfiguration(context: Context): Boolean {
        return try {
            val configText = loadMarkdownFile(context, agentType.configFile)
            configuration = parseConfiguration(configText)
            rules = parseRules(configText)
            workflow = parseWorkflow(configText)
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("Agent", "${agentType.displayName} configuration loaded: ${rules.size} rules, ${workflow.size} workflow steps")
            }
            
            true
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("Agent", "Failed to load configuration for ${agentType.displayName}", e)
            }
            false
        }
    }
    
    override fun getState(): AgentState = _state.value
    
    override fun reset() {
        _state.value = AgentState()
        if (BuildConfig.DEBUG) {
            android.util.Log.d("Agent", "${agentType.displayName} agent reset")
        }
    }
    
    override fun getWorkflow(): List<WorkflowStep> = workflow
    
    protected fun updateState(update: (AgentState) -> AgentState) {
        _state.value = update(_state.value)
    }
    
    /**
     * Load markdown file from assets
     */
    private fun loadMarkdownFile(context: Context, filename: String): String {
        return try {
            context.assets.open("agents/$filename").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("Agent", "Failed to load $filename", e)
            }
            throw e
        }
    }
    
    /**
     * Parse agent configuration from markdown
     */
    private fun parseConfiguration(markdown: String): AgentConfiguration {
        val lines = markdown.lines()
        var role = ""
        var specialization = ""
        var overview = ""
        
        var inOverview = false
        var inRole = false
        
        for (line in lines) {
            when {
                line.startsWith("**Role:**") -> {
                    role = line.substringAfter("**Role:**").trim()
                }
                line.startsWith("**Specialization:**") -> {
                    specialization = line.substringAfter("**Specialization:**").trim()
                }
                line.startsWith("## 🧠 Agent Overview") -> {
                    inOverview = true
                }
                inOverview && line.isNotBlank() && !line.startsWith("#") -> {
                    overview += line + "\n"
                }
                line.startsWith("## ") && inOverview -> {
                    inOverview = false
                }
            }
        }
        
        return AgentConfiguration(
            role = role,
            specialization = specialization,
            overview = overview.trim()
        )
    }
    
    /**
     * Parse rules from markdown configuration
     */
    private fun parseRules(markdown: String): List<AgentRule> {
        val rules = mutableListOf<AgentRule>()
        val lines = markdown.lines()
        var inRulesSection = false
        var currentCategory = ""
        
        for (line in lines) {
            when {
                line.startsWith("## 📋 Core Rules") -> {
                    inRulesSection = true
                    currentCategory = "Core Rules"
                }
                line.startsWith("## ") && inRulesSection -> {
                    inRulesSection = false
                }
                inRulesSection && line.startsWith("- **") -> {
                    val rule = line.substringAfter("- **").substringBefore(":**")
                    val description = line.substringAfter(":**").trim()
                    rules.add(AgentRule(currentCategory, "$rule: $description"))
                }
            }
        }
        
        return rules
    }
    
    /**
     * Parse workflow from markdown configuration
     */
    private fun parseWorkflow(markdown: String): List<WorkflowStep> {
        val steps = mutableListOf<WorkflowStep>()
        val lines = markdown.lines()
        var inWorkflowSection = false
        var stepNumber = 0
        
        for (i in lines.indices) {
            val line = lines[i]
            when {
                line.startsWith("## 🔄 Primary Workflow") -> {
                    inWorkflowSection = true
                }
                line.startsWith("## ") && inWorkflowSection -> {
                    inWorkflowSection = false
                }
                inWorkflowSection && line.startsWith("### Step") -> {
                    stepNumber++
                    val title = line.substringAfter(": ").trim()
                    var description = ""
                    var process = ""
                    
                    // Parse the step details
                    var j = i + 1
                    while (j < lines.size && !lines[j].startsWith("### ") && !lines[j].startsWith("## ")) {
                        val stepLine = lines[j]
                        if (stepLine.startsWith("Process:") || stepLine.startsWith("- ")) {
                            process += stepLine + "\n"
                        } else if (stepLine.isNotBlank()) {
                            description += stepLine + "\n"
                        }
                        j++
                    }
                    
                    steps.add(
                        WorkflowStep(
                            stepNumber = stepNumber,
                            title = title,
                            description = description.trim(),
                            process = process.trim()
                        )
                    )
                }
            }
        }
        
        return steps
    }
    
    /**
     * Check if a rule applies to the current context
     */
    protected fun checkRule(ruleText: String, context: ToolContext): Boolean {
        // Basic rule checking implementation
        // Can be enhanced with more sophisticated rule evaluation
        return true
    }
    
    /**
     * Apply agent rules to validate action
     */
    protected fun validateAction(action: String, context: ToolContext): Boolean {
        return rules.all { rule -> checkRule(rule.rule, context) }
    }
}

/**
 * AgentConfiguration - Parsed configuration from markdown
 */
data class AgentConfiguration(
    val role: String,
    val specialization: String,
    val overview: String
)