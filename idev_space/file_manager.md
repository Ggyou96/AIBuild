# IDev Space - File Manager

## 📁 File Manager Overview
The File Manager is a comprehensive file and project management system within AIBuild's IDev Space, providing developers with powerful tools to organize, edit, and manage their Android project files.

## 🎯 Core Features

### File Operations
- **Create:** Create new files and directories
- **Edit:** In-app code editor with syntax highlighting
- **Delete:** Safe file deletion with confirmation
- **Copy/Move:** File and folder operations
- **Rename:** Bulk and individual file renaming
- **Search:** Advanced file content and name searching

### Project Structure Management
- **Tree View:** Hierarchical project structure visualization
- **Quick Navigation:** Jump to files quickly
- **Bookmarks:** Bookmark frequently accessed files
- **Recent Files:** Access recently modified files
- **File Templates:** Create files from predefined templates

## 🗂️ File System Navigation

### Project Tree Structure
```
📁 AIBuild Project
├── 📁 app/
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 kotlin/
│   │   │   │   └── 📁 com/aibuild/
│   │   │   │       ├── 📁 ui/
│   │   │   │       │   ├── 📁 screens/
│   │   │   │       │   ├── 📁 components/
│   │   │   │       │   └── 📁 theme/
│   │   │   │       ├── 📁 viewmodel/
│   │   │   │       ├── 📁 data/
│   │   │   │       └── 📁 domain/
│   │   │   ├── 📁 res/
│   │   │   └── AndroidManifest.xml
│   │   ├── 📁 test/
│   │   └── 📁 androidTest/
│   └── build.gradle
├── 📁 docs/
├── 📁 agents/
├── 📁 tools/
└── 📁 idev_space/
```

### File Type Icons and Recognition
- **📄 .kt** - Kotlin source files
- **🎨 .xml** - Layout and resource files
- **⚙️ .gradle** - Build configuration files
- **📝 .md** - Markdown documentation
- **🖼️ .png/.jpg** - Image assets
- **🔧 .json** - Configuration files

## ✏️ Code Editor Features

### Syntax Highlighting
- **Kotlin:** Full Kotlin syntax support
- **XML:** Android layout and resource files
- **JSON:** Configuration and data files
- **Markdown:** Documentation files
- **Gradle:** Build script highlighting

### Smart Features
- **Auto-completion:** Context-aware code suggestions
- **Error Detection:** Real-time syntax and error checking
- **Code Formatting:** Automatic code formatting
- **Bracket Matching:** Highlight matching brackets
- **Line Numbers:** Configurable line numbering

### Advanced Editing
- **Multiple Cursors:** Edit multiple locations simultaneously
- **Find and Replace:** Advanced search and replace with regex
- **Code Folding:** Collapse code blocks for better navigation
- **Minimap:** Overview of entire file structure
- **Split View:** Edit multiple files side by side

## 🔍 Search and Navigation

### File Search
- **Global Search:** Search across all project files
- **File Name Search:** Quick file finder
- **Content Search:** Search within file contents
- **Regex Support:** Advanced pattern matching
- **Filter by Type:** Search specific file types

### Advanced Navigation
- **Go to Definition:** Navigate to symbol definitions
- **Find References:** Find all symbol usages
- **Symbol Search:** Search for classes, methods, variables
- **File Outline:** Navigate within large files
- **Breadcrumb Navigation:** Show current file location

## 📋 File Templates

### Android Templates
```kotlin
// Activity Template
class ${ActivityName}Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIBuildTheme {
                ${ScreenName}Screen()
            }
        }
    }
}
```

### Compose Screen Template
```kotlin
// Compose Screen Template
@Composable
fun ${ScreenName}Screen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${Screen Title}",
            style = MaterialTheme.typography.headlineMedium
        )
        // Screen content here
    }
}
```

### ViewModel Template
```kotlin
// ViewModel Template
class ${ScreenName}ViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(${ScreenName}UiState())
    val uiState: StateFlow<${ScreenName}UiState> = _uiState.asStateFlow()
    
    // ViewModel logic here
}
```

## 🔧 Project Management Tools

### Build System Integration
- **Gradle Sync:** Trigger Gradle synchronization
- **Build Status:** Monitor build progress
- **Dependency Management:** Add/remove dependencies
- **Build Variants:** Manage debug/release configurations

### Version Control Integration
- **Git Status:** Show file modification status
- **Commit Changes:** Stage and commit files
- **Branch Management:** Switch between branches
- **Conflict Resolution:** Resolve merge conflicts

### Code Quality Tools
- **Lint Integration:** Real-time linting feedback
- **Code Analysis:** Static code analysis
- **Formatting:** Auto-format code on save
- **Import Organization:** Organize imports automatically

## 📊 File Analytics

### Project Statistics
- **Line Count:** Total lines of code by language
- **File Count:** Number of files by type
- **Project Size:** Total project size and growth
- **Complexity Metrics:** Code complexity analysis

### File History
- **Change Tracking:** Track file modifications
- **Version History:** View file version history
- **Backup Management:** Automatic file backups
- **Recovery Tools:** Restore deleted or corrupted files

## 🎨 Customization Options

### Editor Preferences
- **Theme Selection:** Dark/light editor themes
- **Font Configuration:** Font family and size settings
- **Color Schemes:** Customizable syntax highlighting
- **Indentation:** Tab vs. spaces configuration

### Layout Customization
- **Panel Arrangement:** Customize panel layout
- **Toolbar Configuration:** Show/hide toolbar buttons
- **File Tree Options:** Configure tree view display
- **Split Pane Settings:** Adjust split view behavior

## 🚀 Productivity Features

### Quick Actions
- **File Creation Shortcuts:** Keyboard shortcuts for new files
- **Recent Files Menu:** Quick access to recent files
- **Favorite Files:** Pin important files for quick access
- **Workspace Management:** Save and restore workspace layouts

### Batch Operations
- **Multi-File Edit:** Edit multiple files simultaneously
- **Bulk Rename:** Rename multiple files at once
- **Mass Search/Replace:** Replace across multiple files
- **File Conversion:** Convert between file formats

### Integration Features
- **External Editor Support:** Open files in external editors
- **File Sharing:** Share files with team members
- **Export Options:** Export files in various formats
- **Import Tools:** Import files from external sources

## 📱 Mobile-Optimized Features

### Touch-Friendly Interface
- **Gesture Navigation:** Swipe gestures for navigation
- **Touch Targets:** Optimized button and link sizes
- **Responsive Design:** Adapts to different screen sizes
- **One-Handed Mode:** Optimized for one-handed operation

### Mobile-Specific Tools
- **Voice Input:** Voice-to-text for code comments
- **Camera Integration:** Import images directly from camera
- **Offline Mode:** Continue working without internet
- **Cloud Sync:** Automatic cloud synchronization

## 🔐 Security and Backup

### Data Protection
- **Encryption:** File encryption for sensitive data
- **Access Control:** File and folder permissions
- **Audit Trail:** Track file access and modifications
- **Secure Sharing:** Encrypted file sharing

### Backup and Recovery
- **Auto-Save:** Automatic file saving
- **Version Backup:** Multiple file versions
- **Cloud Backup:** Automatic cloud backup
- **Recovery Tools:** File recovery and restoration

---

## 💡 Usage Examples

### Creating a New Screen
1. Right-click in `ui/screens/` folder
2. Select "New File from Template"
3. Choose "Compose Screen Template"
4. Enter screen name: "Profile"
5. File created: `ProfileScreen.kt`

### Searching Across Project
1. Press Ctrl+Shift+F (or use search button)
2. Enter search term: "HomeScreen"
3. Select scope: "Entire Project"
4. View results with context

### Setting Up File Watchers
1. Go to File Manager Settings
2. Enable "Auto-format on Save"
3. Configure lint rules
4. Set up import organization

---
*AIBuild IDev Space File Manager - Version 1.0*