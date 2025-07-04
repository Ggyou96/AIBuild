# AIBuild

🎯 **AIBuild** is the world's first mobile-first AI-native development platform powered by Koog.ai Agents, built with **Kotlin** and **Jetpack Compose**.

---

## 📱 Project Overview

AIBuild revolutionizes Android app development by enabling developers to plan, build, and deploy applications directly on their mobile devices using intelligent AI agents. The platform features:

- 🤖 **AI Agent Collaboration** - Work with specialized Planner, Teammate, and Builder agents
- 📱 **Mobile-First Development** - Complete development workflow optimized for mobile devices  
- 🔧 **Integrated Development Environment** - Full-featured IDev Space with file management, monitoring, and diagnostics
- 🎨 **Modern UI/UX** - Material Design 3 with dark/light theme support

---

## 🏗️ Project Structure

```
AIBuild/
├── 📄 README.md                    # Project documentation
├── 📄 STATUS.md                    # Setup completion status
├── 📄 build.gradle.kts             # Project-level build configuration
├── � settings.gradle.kts          # Project settings
├── 📁 app/                         # Main Android application
│   ├── 📄 build.gradle.kts         # App-level build configuration
│   └── 📁 src/main/
│       ├── 📄 AndroidManifest.xml  # App manifest
│       ├── 📁 kotlin/com/aibuild/
│       │   ├── 📄 AIBuildApplication.kt  # Application class
│       │   └── 📁 ui/
│       │       ├── 📄 MainActivity.kt    # Main activity
│       │       ├── 📁 navigation/        # Navigation system
│       │       ├── 📁 screens/           # Screen implementations
│       │       └── 📁 theme/             # Material Design theme
│       └── 📁 res/                  # Android resources
├── 📁 docs/                        # Documentation templates
│   ├── 📄 PRD_TEMPLATE.md          # Product Requirements Document
│   ├── 📄 Plan_TEMPLATE.md         # Development Plan template
│   ├── 📄 RoadMap_TEMPLATE.md      # Project Roadmap template
│   └── 📄 Stage_Plan_TEMPLATE.md   # Stage Plan template
├── 📁 agents/                      # AI Agent configurations
│   ├── 📄 planner_agent.md         # Planner Agent config
│   ├── 📄 teammate_agent.md        # Teammate Agent config
│   └── 📄 builder_agent.md         # Builder Agent config
├── 📁 tools/                       # Tool documentation
│   ├── 📄 built_in_tools.md        # Built-in tools reference
│   └── 📄 developer_tools.md       # Developer tools reference
└── 📁 idev_space/                  # IDev Space documentation
    ├── 📄 file_manager.md          # File Manager documentation
    ├── 📄 monitoring_diagnostics.md # Monitoring & Diagnostics docs
    └── 📄 agent_workspace.md       # Agent Workspace documentation
```

---

## � App Features

### 🏠 Home Screen
- **AI Chat Panel** - Interactive chat with toggle between Planner and Teammate agents
- **Live Projects** - Project management and tracking (empty state ready for implementation)
- **Top Community Apps** - Showcase of popular community-built applications
- **Project Description** - Comprehensive information about the AIBuild platform

### 🔧 IDev Space
- **Files Manager** - Complete file management system with project tree navigation
- **Monitoring & Diagnostics** - Real-time performance monitoring and debugging tools  
- **Agents Workspace** - Collaborative environment for AI agent interactions

### ⚙️ Settings
- **App Settings** - Dark mode, notifications, auto-save, and language preferences
- **Developer Options** - Debug mode, performance overlay, beta features, and build configuration
- **Agent Configuration** - Customize AI agent behavior and interaction preferences
- **Account & Profile** - Profile management, sync settings, privacy, and backup options

---

## 🤖 AI Agent System

### Planner Agent
- **Specialization**: Project planning and requirements analysis
- **Workflow**: Idea analysis → PRD generation → Market research → Accept/Reject → Plan creation
- **Rule**: Real only. Illusion forbidden.

### Teammate Agent  
- **Specialization**: Code analysis and developer guidance
- **Workflow**: Project file analysis → Component explanation → User guidance → Progress tracking
- **Rule**: Analyze project files, explain everything clearly

### Builder Agent
- **Specialization**: Code implementation and development
- **Workflow**: Prerequisites verification → Stage planning → Documentation review → Code implementation
- **Rule**: Build only after Plan.md & RoadMap.md exist

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Clean Architecture  
- **Dependency Injection**: Hilt/Dagger
- **Navigation**: Compose Navigation
- **Database**: Room (configured)
- **Networking**: Retrofit + OkHttp (configured)
- **Image Loading**: Coil (configured)
- **Testing**: JUnit, Espresso, Compose Testing

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Flamingo or newer
- Kotlin 1.9.22+
- Android SDK 34
- Gradle 8.2.1+

### Setup Steps

1. **Clone the repository:**
```bash
git clone https://github.com/Ggyou96/AIBuild.git
cd AIBuild
```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned AIBuild directory

3. **Build and run:**
```bash
./gradlew build
```

4. **Run on device/emulator:**
   - Connect your Android device or start an emulator
   - Click the "Run" button in Android Studio

### First Launch
The app will start with the Home screen, featuring the AI chat interface and navigation to all three main sections. All screens are functional with placeholder content ready for further development.

---

## 📊 Development Status

✅ **Complete** - Project setup and architecture  
✅ **Complete** - UI/UX implementation with Material Design 3  
✅ **Complete** - Navigation system with bottom tabs  
✅ **Complete** - Agent system framework and documentation  
✅ **Complete** - IDev Space module structure  
🔄 **Planned** - Real AI agent integration  
🔄 **Planned** - File management implementation  
🔄 **Planned** - Performance monitoring system  
🔄 **Planned** - Code generation capabilities  

---

## 🤝 Contributing

AIBuild is designed to be self-improving through AI agent collaboration. The platform structure supports:

- Agent-assisted feature development
- Automated code generation and review
- Intelligent testing and optimization
- Self-building capabilities

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 🙏 Acknowledgments

- **Koog.ai** - For powering the AI agent technology
- **Jetpack Compose** - For the modern UI framework
- **Material Design** - For the design system
- **Android Community** - For the development tools and best practices

---

**AIBuild** - Revolutionizing mobile development with AI 🚀

Made with 💙 by the AIBuild Team
