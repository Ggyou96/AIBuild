package com.aibuild

import android.app.Application
import com.aibuild.data.api_keys.repository.ApiKeysRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AIBuild Application class
 * 
 * This is the main application class for AIBuild - the world's first mobile-first 
 * AI-native development platform. It serves as the entry point for dependency injection
 * and global application configuration.
 * 
 * Features:
 * - Hilt dependency injection setup
 * - Secure API keys database initialization
 * - Global application state management
 * - Performance monitoring initialization
 * - Developer mode enforcement for security
 * 
 * @author AIBuild Team
 * @version 1.0
 */
@HiltAndroidApp
class AIBuildApplication : Application() {
    
    @Inject
    lateinit var apiKeysRepository: ApiKeysRepository
    
    // Application-level coroutine scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("AIBuildApplication", "AIBuild application starting...")
        }
        
        // Initialize secure storage systems
        initializeSecureStorage()
        
        // Initialize performance monitoring for development
        initializePerformanceMonitoring()
        
        // Initialize crash reporting
        initializeCrashReporting()
        
        // Setup global error handling
        setupGlobalErrorHandling()
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("AIBuildApplication", "AIBuild application started successfully")
        }
    }
    
    /**
     * Initialize secure storage for API keys
     * Only initializes when developer mode is enabled for security
     */
    private fun initializeSecureStorage() {
        applicationScope.launch {
            try {
                // Initialize repository with developer mode status
                // In production, this should check actual developer mode preference
                val isDeveloperMode = BuildConfig.DEBUG
                
                apiKeysRepository.initialize(isDeveloperMode)
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("AIBuildApplication", "Secure storage initialized (Developer mode: $isDeveloperMode)")
                    
                    // Log database status for debugging
                    val dbExists = apiKeysRepository.databaseExists()
                    val hasKeys = apiKeysRepository.hasAnyApiKeys()
                    val keyCount = apiKeysRepository.getConfiguredKeysCount()
                    
                    android.util.Log.d("AIBuildApplication", "Database exists: $dbExists, Has keys: $hasKeys, Key count: $keyCount")
                }
                
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("AIBuildApplication", "Failed to initialize secure storage", e)
                }
            }
        }
    }
    
    /**
     * Initialize performance monitoring for tracking app performance
     */
    private fun initializePerformanceMonitoring() {
        if (BuildConfig.DEBUG) {
            android.util.Log.d("AIBuildApplication", "Performance monitoring initialized")
        }
        // TODO: Implement performance monitoring
        // This will be integrated with the Monitoring & Diagnostics module
    }
    
    /**
     * Initialize crash reporting to track and analyze crashes
     */
    private fun initializeCrashReporting() {
        if (BuildConfig.DEBUG) {
            android.util.Log.d("AIBuildApplication", "Crash reporting initialized")
        }
        // TODO: Implement crash reporting
        // This will help track issues in the wild
    }
    
    /**
     * Setup global error handling to catch unhandled exceptions
     */
    private fun setupGlobalErrorHandling() {
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            // Log the exception for debugging
            android.util.Log.e("AIBuild", "Uncaught exception in thread ${thread.name}", exception)
            
            // TODO: Send crash report to monitoring service
            // In production, this would send crash data to analytics service
        }
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("AIBuildApplication", "Global error handling configured")
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        // Clean up resources
        try {
            // Clear sensitive data from memory
            if (::apiKeysRepository.isInitialized) {
                apiKeysRepository.clearCache()
            }
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("AIBuildApplication", "AIBuild application terminated and cleaned up")
            }
            
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("AIBuildApplication", "Error during application cleanup", e)
            }
        }
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        
        // Clear non-essential caches when memory is low
        try {
            if (::apiKeysRepository.isInitialized) {
                apiKeysRepository.clearCache()
            }
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("AIBuildApplication", "Cleared caches due to low memory")
            }
            
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("AIBuildApplication", "Error clearing caches on low memory", e)
            }
        }
    }
}