package com.aibuild.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aibuild.R
import com.aibuild.ui.screens.home.HomeScreen
import com.aibuild.ui.screens.idevspace.IDevSpaceScreen
import com.aibuild.ui.screens.idevspace.AgentsWorkspaceScreen
import com.aibuild.ui.screens.idevspace.FilesManagerScreen
import com.aibuild.ui.screens.idevspace.MonitoringDiagnosticsScreen
import com.aibuild.ui.screens.settings.SettingsScreen

/**
 * Main navigation destinations for the AIBuild app
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object IDevSpace : Screen("idev_space", "IDev Space", Icons.Filled.Build)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    
    // IDev Space sub-screens
    object FilesManager : Screen("files_manager", "Files Manager", Icons.Filled.Build)
    object MonitoringDiagnostics : Screen("monitoring", "Monitoring", Icons.Filled.Build)
    object AgentsWorkspace : Screen("agents_workspace", "Agents", Icons.Filled.Build)
}

/**
 * Main navigation items for bottom navigation
 */
val bottomNavigationItems = listOf(
    Screen.Home,
    Screen.IDevSpace,
    Screen.Settings
)

/**
 * Main navigation composable for the AIBuild app
 * 
 * Provides the main navigation structure with bottom navigation bar
 * and handles navigation between the three main sections of the app.
 * 
 * @param navController Navigation controller for managing navigation
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun AIBuildNavigation(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AIBuildBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main tabs
            composable(Screen.Home.route) {
                HomeScreen()
            }
            
            composable(Screen.IDevSpace.route) {
                IDevSpaceScreen(
                    onNavigateToFilesManager = {
                        navController.navigate(Screen.FilesManager.route)
                    },
                    onNavigateToMonitoring = {
                        navController.navigate(Screen.MonitoringDiagnostics.route)
                    },
                    onNavigateToAgentsWorkspace = {
                        navController.navigate(Screen.AgentsWorkspace.route)
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            
            // IDev Space sub-screens
            composable(Screen.FilesManager.route) {
                FilesManagerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.MonitoringDiagnostics.route) {
                MonitoringDiagnosticsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.AgentsWorkspace.route) {
                AgentsWorkspaceScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Bottom navigation bar component
 * 
 * Displays the three main navigation tabs with icons and labels.
 * 
 * @param navController Navigation controller for handling navigation
 */
@Composable
fun AIBuildBottomNavigation(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavigationItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}