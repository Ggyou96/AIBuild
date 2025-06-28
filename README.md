AIBuild

🎯 **AIBuild** is a modular Android application template built with **Kotlin** and **Jetpack Compose**, designed to empower developers with a clean architecture and smart agent integrations.

---

## 📱 Project Overview

This app is a UI-only template that sets the foundation for building advanced Android applications powered by AI tools. It includes:
- ✅ Clean modular file structure  
- 🎨 Jetpack Compose UI components  
- 🧠 Developer Agent interface for command execution  
- 🔧 Settings page with biometric auth, language selection, and API key management  

---

## 📁 File Structure

app/src/main/kotlin/com/aibuild/pages/ ├── Home/ │   └── features/ │       └── Intro/ │           ├── core/ │           └── ui/ ├── DeveloperAgent/ │   └── features/ │       └── AgentConsole/ │           ├── core/ │           └── ui/ └── Settings/ └── features/ ├── BiometricAuth/ │   ├── core/ │   └── ui/ ├── Language/ │   ├── core/ │   └── ui/ └── APIKeys/ ├── core/ └── ui/

---

## 📄 UI Pages Implemented

### 🏠 HomeScreen
- Title and description of AIBuild  
- Centered layout using Jetpack Compose

### 🧠 DeveloperAgentScreen
- TextField for commands  
- Console-style output area  
- Agent status display

### ⚙️ Settings
- **BiometricAuthSetting**: Switch to enable/disable biometrics  
- **LanguageSetting**: Dropdown for selecting app language  
- **ApiKeysSetting**: Manage API keys for multiple services  

---

## 🛠 Tech Stack
- 💻 Kotlin  
- 🎨 Jetpack Compose (Material 3)  
- 🧱 Modular architecture  
- 🧪 Stateless UI components (no ViewModels, no logic)

---

## 🚀 How to Build

1. Clone the repo:
```bash
git clone https://github.com/ggyou96/AIBuild.git

2. Open in Android Studio.


3. Build & run using:



./gradlew build


---

🧠 Note

This project is UI-only and designed to be extended with actual logic, navigation, and integrations.
It is also prepared to work with smart developer agents for future automation of app building.


---

Made with 💙 by AIBuild Dev
