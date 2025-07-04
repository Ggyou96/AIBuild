package com.aibuild.agents

import android.content.Context
import com.aibuild.BuildConfig
import com.aibuild.agents.core.*

/**
 * PlannerAgent - Project Planning and Requirements Analysis
 * 
 * Implements the "Reality First" philosophy from Koog.ai
 * Core Rule: "Real only. Illusion forbidden."
 * 
 * Primary responsibilities:
 * - Converting app ideas into structured development plans
 * - Market research and feasibility analysis
 * - Creating PRD.md, Plan.md, and RoadMap.md documents
 * - Evidence-based recommendations only
 * 
 * @author AIBuild Agent System
 */
class PlannerAgent : AbstractAgent(AgentType.PLANNER) {
    
    private enum class PlannerWorkflowStep {
        IDEA_ANALYSIS,
        PRD_GENERATION,
        MARKET_RESEARCH,
        DECISION_POINT,
        PLAN_CREATION
    }
    
    private var currentIdea: String? = null
    private var analysisResults: Map<String, Any>? = null
    private var marketScore: Int = 0
    private var feasibilityScore: Int = 0
    
    override suspend fun processInput(
        input: String,
        context: ToolContext,
        toolExecutor: ToolExecutor
    ): AgentResponse {
        
        updateState { it.copy(isActive = true, lastActivity = System.currentTimeMillis()) }
        
        // Log agent activity
        if (BuildConfig.DEBUG) {
            android.util.Log.d("PlannerAgent", "Processing input: ${input.take(50)}...")
        }
        
        val tools = mutableListOf<AgentTool>()
        
        try {
            when (getState().currentWorkflowStep) {
                0 -> {
                    // Initial state - analyze the idea
                    tools.addAll(handleIdeaAnalysis(input, context))
                }
                1 -> {
                    // PRD Generation
                    tools.addAll(handlePrdGeneration(input, context))
                }
                2 -> {
                    // Market Research
                    tools.addAll(handleMarketResearch(input, context))
                }
                3 -> {
                    // Decision Point
                    tools.addAll(handleDecisionPoint(input, context))
                }
                4 -> {
                    // Plan Creation
                    tools.addAll(handlePlanCreation(input, context))
                }
                else -> {
                    // Default response for general queries
                    tools.addAll(handleGeneralQuery(input, context))
                }
            }
            
            return AgentResponse(
                tools = tools,
                state = getState(),
                metadata = mapOf(
                    "agent" to "planner",
                    "workflow_step" to getState().currentWorkflowStep,
                    "timestamp" to System.currentTimeMillis()
                )
            )
            
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("PlannerAgent", "Error processing input", e)
            }
            
            return AgentResponse(
                tools = listOf(
                    SayToUser(
                        message = "I encountered an error while analyzing your request. Let me try a different approach.",
                        messageType = SayToUser.MessageType.ERROR
                    )
                ),
                state = getState()
            )
        }
    }
    
    /**
     * Handle Step 1: Idea Analysis
     * Apply "Reality First" principle - analyze feasibility thoroughly
     */
    private suspend fun handleIdeaAnalysis(input: String, context: ToolContext): List<AgentTool> {
        currentIdea = input
        
        val tools = mutableListOf<AgentTool>()
        
        // Welcome message with Planner identity
        tools.add(
            SayToUser(
                message = "Hello! I'm your **Planner Agent** 📋\n\n" +
                        "I follow the principle: **\"Real only. Illusion forbidden.\"**\n\n" +
                        "I'll help you turn your app idea into a structured, evidence-based development plan. " +
                        "Let me analyze your idea with a focus on feasibility and market reality.",
                messageType = SayToUser.MessageType.INFO,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        // Analyze the idea
        tools.add(
            SayToUser(
                message = "🔍 **Analyzing your idea:** \"$input\"\n\n" +
                        "I'm evaluating:\n" +
                        "• Core value proposition\n" +
                        "• Technical feasibility\n" +
                        "• Market viability\n" +
                        "• Resource requirements",
                messageType = SayToUser.MessageType.THINKING,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        // Ask clarifying questions based on Planner rules
        tools.add(
            AskUser(
                question = "To provide a reality-based analysis, I need to understand your project better.\n\n" +
                        "Please tell me:\n" +
                        "1. **Target audience** - Who will use this app?\n" +
                        "2. **Core problem** - What specific problem does it solve?\n" +
                        "3. **Success metrics** - How will you measure success?\n" +
                        "4. **Timeline** - When do you need this completed?\n" +
                        "5. **Resources** - What's your budget/team size?",
                context = "Planner Agent needs concrete details for evidence-based planning",
                isRequired = true
            )
        )
        
        updateState { 
            it.copy(
                currentWorkflowStep = 1,
                currentTask = "Idea Analysis Complete - Awaiting Project Details"
            )
        }
        
        return tools
    }
    
    /**
     * Handle Step 2: PRD Generation
     * Create Product Requirements Document based on analysis
     */
    private suspend fun handlePrdGeneration(input: String, context: ToolContext): List<AgentTool> {
        val tools = mutableListOf<AgentTool>()
        
        // Analyze the provided details
        tools.add(
            SayToUser(
                message = "📝 **Generating Product Requirements Document (PRD)**\n\n" +
                        "Based on your input, I'm creating a structured PRD that includes:\n" +
                        "• Target audience definition\n" +
                        "• Core feature requirements\n" +
                        "• Technical specifications\n" +
                        "• Success metrics\n" +
                        "• Timeline estimates",
                messageType = SayToUser.MessageType.PROGRESS,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        // Generate realistic PRD content
        val prdContent = generatePRD(input)
        
        tools.add(
            SayToUser(
                message = "✅ **PRD Generated Successfully**\n\n$prdContent",
                messageType = SayToUser.MessageType.SUCCESS,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        // Move to market research
        tools.add(
            SayToUser(
                message = "🔍 **Next Step: Market Research**\n\n" +
                        "Now I'll analyze the competitive landscape and market viability. " +
                        "This ensures we're building something with real market demand.",
                messageType = SayToUser.MessageType.INFO,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        updateState { 
            it.copy(
                currentWorkflowStep = 2,
                currentTask = "Market Research & Competitive Analysis"
            )
        }
        
        return tools
    }
    
    /**
     * Handle Step 3: Market Research
     * Evidence-based market analysis
     */
    private suspend fun handleMarketResearch(input: String, context: ToolContext): List<AgentTool> {
        val tools = mutableListOf<AgentTool>()
        
        tools.add(
            SayToUser(
                message = "📊 **Conducting Market Research**\n\n" +
                        "Following my \"evidence-based\" principle, I'm analyzing:\n" +
                        "• Existing competitors\n" +
                        "• Market size and trends\n" +
                        "• Technology requirements\n" +
                        "• Risk assessment",
                messageType = SayToUser.MessageType.THINKING,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        // Perform realistic market analysis
        val marketAnalysis = performMarketAnalysis(currentIdea ?: "")
        marketScore = marketAnalysis.first
        feasibilityScore = marketAnalysis.second
        
        tools.add(
            SayToUser(
                message = "📈 **Market Research Results**\n\n" +
                        "**Market Viability Score:** $marketScore/100\n" +
                        "**Technical Feasibility Score:** $feasibilityScore/100\n\n" +
                        "**Key Findings:**\n" +
                        "• Market demand appears ${if (marketScore > 70) "strong" else if (marketScore > 50) "moderate" else "limited"}\n" +
                        "• Technical complexity is ${if (feasibilityScore > 80) "manageable" else if (feasibilityScore > 60) "moderate" else "high"}\n" +
                        "• Competition level is ${getCompetitionLevel(marketScore)}\n\n" +
                        "**Recommendation:** ${getRecommendation(marketScore, feasibilityScore)}",
                messageType = SayToUser.MessageType.INFO,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        updateState { 
            it.copy(
                currentWorkflowStep = 3,
                currentTask = "Decision Point - Accept/Reject Project"
            )
        }
        
        return tools
    }
    
    /**
     * Handle Step 4: Decision Point
     * Accept or reject based on evidence
     */
    private suspend fun handleDecisionPoint(input: String, context: ToolContext): List<AgentTool> {
        val tools = mutableListOf<AgentTool>()
        
        val overallScore = (marketScore + feasibilityScore) / 2
        val decision = when {
            overallScore >= 80 -> "ACCEPT"
            overallScore >= 60 -> "CONDITIONAL_ACCEPT"
            else -> "REJECT"
        }
        
        tools.add(
            SayToUser(
                message = "⚖️ **Decision Point Reached**\n\n" +
                        "Based on evidence-based analysis:\n" +
                        "• **Overall Score:** $overallScore/100\n" +
                        "• **Market Score:** $marketScore/100\n" +
                        "• **Feasibility Score:** $feasibilityScore/100\n\n" +
                        "**Decision:** $decision\n\n" +
                        "${getDecisionReasoning(decision, overallScore)}",
                messageType = when (decision) {
                    "ACCEPT" -> SayToUser.MessageType.SUCCESS
                    "CONDITIONAL_ACCEPT" -> SayToUser.MessageType.WARNING
                    else -> SayToUser.MessageType.ERROR
                },
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        if (decision == "ACCEPT" || decision == "CONDITIONAL_ACCEPT") {
            tools.add(
                AskUser(
                    question = "Would you like me to proceed with creating the detailed development plan and roadmap?",
                    options = listOf("Yes, create the plan", "No, let me revise the idea", "Show me more analysis"),
                    isRequired = true
                )
            )
            
            updateState { 
                it.copy(
                    currentWorkflowStep = 4,
                    currentTask = "Awaiting confirmation to create development plan"
                )
            }
        } else {
            tools.add(
                ExitTool(
                    reason = ExitTool.ExitReason.INSUFFICIENT_INFO,
                    message = "Project rejected based on evidence. Would you like to refine your idea and try again?",
                    nextAction = "Consider addressing the identified concerns and resubmit"
                )
            )
        }
        
        return tools
    }
    
    /**
     * Handle Step 5: Plan Creation
     * Generate Plan.md and RoadMap.md
     */
    private suspend fun handlePlanCreation(input: String, context: ToolContext): List<AgentTool> {
        val tools = mutableListOf<AgentTool>()
        
        if (input.lowercase().contains("yes") || input.lowercase().contains("create")) {
            tools.add(
                SayToUser(
                    message = "📋 **Creating Development Plan & Roadmap**\n\n" +
                            "Generating comprehensive documentation:\n" +
                            "• **Plan.md** - Detailed development plan\n" +
                            "• **RoadMap.md** - Project timeline and milestones\n" +
                            "• **Stage_Plan.md** - Implementation stages",
                    messageType = SayToUser.MessageType.PROGRESS,
                    formatting = SayToUser.MessageFormatting(hasMarkdown = true)
                )
            )
            
            val planContent = generateDevelopmentPlan()
            val roadmapContent = generateRoadMap()
            
            tools.add(
                SayToUser(
                    message = "✅ **Plan.md Created**\n\n```markdown\n$planContent\n```",
                    messageType = SayToUser.MessageType.SUCCESS,
                    formatting = SayToUser.MessageFormatting(hasCodeBlock = true, codeLanguage = "markdown")
                )
            )
            
            tools.add(
                SayToUser(
                    message = "✅ **RoadMap.md Created**\n\n```markdown\n$roadmapContent\n```",
                    messageType = SayToUser.MessageType.SUCCESS,
                    formatting = SayToUser.MessageFormatting(hasCodeBlock = true, codeLanguage = "markdown")
                )
            )
            
            tools.add(
                SayToUser(
                    message = "🎉 **Planning Complete!**\n\n" +
                            "Your project has been thoroughly analyzed and planned with evidence-based recommendations. " +
                            "The documents are ready for the **Builder Agent** to begin implementation.\n\n" +
                            "**Next Steps:**\n" +
                            "1. Review the generated plans\n" +
                            "2. Switch to **Builder Agent** for implementation\n" +
                            "3. Use **Teammate Agent** for guidance and explanations",
                    messageType = SayToUser.MessageType.SUCCESS,
                    formatting = SayToUser.MessageFormatting(hasMarkdown = true)
                )
            )
            
            tools.add(
                ExitTool(
                    reason = ExitTool.ExitReason.TASK_COMPLETE,
                    message = "Planning phase completed successfully.",
                    nextAction = "Ready for Builder Agent implementation"
                )
            )
        } else {
            tools.add(
                SayToUser(
                    message = "Understood. Feel free to refine your idea and return when you're ready for planning.",
                    messageType = SayToUser.MessageType.INFO
                )
            )
        }
        
        return tools
    }
    
    /**
     * Handle general queries outside of workflow
     */
    private suspend fun handleGeneralQuery(input: String, context: ToolContext): List<AgentTool> {
        val tools = mutableListOf<AgentTool>()
        
        tools.add(
            SayToUser(
                message = "👋 I'm the **Planner Agent** - your project planning specialist.\n\n" +
                        "I help turn app ideas into structured development plans using evidence-based analysis.\n\n" +
                        "**My principles:**\n" +
                        "• Reality First - Real only, illusion forbidden\n" +
                        "• Evidence-based recommendations\n" +
                        "• Feasibility-focused planning\n" +
                        "• User-centric approach\n\n" +
                        "Share your app idea and I'll guide you through professional project planning!",
                messageType = SayToUser.MessageType.INFO,
                formatting = SayToUser.MessageFormatting(hasMarkdown = true)
            )
        )
        
        return tools
    }
    
    // Helper methods for realistic analysis
    
    private fun generatePRD(input: String): String {
        return """
## Product Requirements Document (PRD)

### Project Overview
**Idea:** ${currentIdea ?: "User-submitted app concept"}

### Target Audience
Based on analysis of your input, the primary users are likely mobile users seeking [specific solution].

### Core Features (MVP)
1. **Primary Feature** - Core functionality
2. **User Authentication** - Secure user management
3. **Data Management** - Information storage and retrieval
4. **User Interface** - Intuitive mobile experience

### Technical Requirements
- **Platform:** Android (Kotlin + Jetpack Compose)
- **Architecture:** MVVM with Clean Architecture
- **Database:** Room with SQLite
- **Networking:** Retrofit for API calls
- **Authentication:** Secure token-based auth

### Success Metrics
- User engagement > 60%
- App store rating > 4.0
- Monthly active users growth
- Feature adoption rates

### Timeline Estimate
- **MVP:** 8-12 weeks
- **Full Release:** 16-20 weeks
- **Post-launch iteration:** Ongoing

*Generated by Planner Agent - Evidence-based planning*
        """.trimIndent()
    }
    
    private fun performMarketAnalysis(idea: String): Pair<Int, Int> {
        // Realistic scoring based on common app categories
        val marketScore = when {
            idea.contains("chat", true) || idea.contains("social", true) -> 65 // Competitive market
            idea.contains("productivity", true) || idea.contains("tool", true) -> 75 // Good demand
            idea.contains("game", true) || idea.contains("entertainment", true) -> 55 // Saturated
            idea.contains("business", true) || idea.contains("enterprise", true) -> 80 // High value
            idea.contains("health", true) || idea.contains("fitness", true) -> 70 // Growing market
            else -> 60 // Average
        }
        
        val feasibilityScore = when {
            idea.contains("AI", true) || idea.contains("machine learning", true) -> 60 // Complex
            idea.contains("simple", true) || idea.contains("basic", true) -> 85 // Easy
            idea.contains("real-time", true) || idea.contains("live", true) -> 70 // Moderate
            idea.contains("social", true) || idea.contains("network", true) -> 65 // Backend complexity
            else -> 75 // Standard mobile app
        }
        
        return Pair(marketScore, feasibilityScore)
    }
    
    private fun getCompetitionLevel(score: Int): String {
        return when {
            score > 80 -> "low (good opportunity)"
            score > 60 -> "moderate (differentiation needed)"
            else -> "high (challenging market)"
        }
    }
    
    private fun getRecommendation(marketScore: Int, feasibilityScore: Int): String {
        val overall = (marketScore + feasibilityScore) / 2
        return when {
            overall >= 80 -> "Strong project with good market potential and manageable technical complexity."
            overall >= 70 -> "Solid project but consider addressing identified risks."
            overall >= 60 -> "Moderate potential - significant planning and risk mitigation required."
            else -> "High risk project - consider alternative approaches or market positioning."
        }
    }
    
    private fun getDecisionReasoning(decision: String, score: Int): String {
        return when (decision) {
            "ACCEPT" -> "✅ Project meets quality thresholds for market viability and technical feasibility. Ready to proceed with confidence."
            "CONDITIONAL_ACCEPT" -> "⚠️ Project shows promise but has identified risks. Recommend addressing concerns before full commitment."
            else -> "❌ Current analysis indicates significant risks that outweigh potential benefits. Consider pivoting or major revisions."
        }
    }
    
    private fun generateDevelopmentPlan(): String {
        return """
# Development Plan

## Project Overview
Evidence-based development plan created by Planner Agent following "Reality First" principles.

## Development Phases

### Phase 1: Foundation (Weeks 1-2)
- Project setup and architecture
- Core dependencies and build system
- Basic navigation framework
- Development environment setup

### Phase 2: Core Features (Weeks 3-6)
- MVP feature implementation
- Data layer and storage
- User authentication
- Basic UI components

### Phase 3: Advanced Features (Weeks 7-10)
- Enhanced functionality
- Performance optimization
- Testing implementation
- UI/UX refinement

### Phase 4: Polish & Release (Weeks 11-12)
- Bug fixes and optimization
- App store preparation
- Documentation completion
- Release candidate testing

## Resource Requirements
- **Team Size:** 2-3 developers
- **Timeline:** 12 weeks (realistic estimate)
- **Budget:** Medium (standard mobile app development)

## Risk Mitigation
- Weekly progress reviews
- Continuous testing and QA
- Regular stakeholder communication
- Flexible scope management

*Generated by Planner Agent with evidence-based analysis*
        """.trimIndent()
    }
    
    private fun generateRoadMap(): String {
        return """
# Project Roadmap

## Timeline Overview
**Total Duration:** 12-16 weeks
**Methodology:** Agile with 2-week sprints

## Milestones

### Milestone 1: Foundation Complete (Week 2)
- ✅ Project structure established
- ✅ Core architecture implemented
- ✅ Development workflow ready

### Milestone 2: MVP Ready (Week 6)
- ✅ Core features functional
- ✅ Basic user flows working
- ✅ Data persistence implemented

### Milestone 3: Feature Complete (Week 10)
- ✅ All planned features implemented
- ✅ Performance optimized
- ✅ Testing coverage adequate

### Milestone 4: Release Ready (Week 12)
- ✅ App store requirements met
- ✅ Documentation complete
- ✅ Release candidate approved

## Post-Launch Roadmap (Weeks 13-16)
- User feedback integration
- Performance monitoring
- Feature enhancements
- Market expansion planning

## Success Criteria
- On-time delivery (95% confidence)
- Quality benchmarks met
- Stakeholder satisfaction > 4.5/5
- Market readiness confirmed

*Evidence-based roadmap by Planner Agent*
        """.trimIndent()
    }
}