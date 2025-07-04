package com.aibuild.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.aibuild.ui.navigation.AIBuildNavigation
import com.aibuild.ui.theme.AIBuildTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - The main entry point for the AIBuild app
 * 
 * This activity serves as the host for the entire AIBuild application,
 * providing the main navigation structure and theme setup.
 * 
 * The app features:
 * - Bottom navigation with 3 main tabs (Home, IDev Space, Settings)
 * - Material Design 3 theming
 * - Edge-to-edge display support
 * - Jetpack Compose UI throughout
 * 
 * @author AIBuild Team
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for modern Android experience
        enableEdgeToEdge()
        
        setContent {
            AIBuildTheme {
                AIBuildApp()
            }
        }
    }
}

/**
 * Main app composable that sets up the overall app structure
 */
@Composable
fun AIBuildApp() {
    val navController = rememberNavController()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            AIBuildNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIBuildAppPreview() {
    AIBuildTheme {
        AIBuildApp()
    }
}