package com.aibuild

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * AIBuild Application class
 * 
 * This is the main application class for AIBuild - the world's first mobile-first 
 * AI-native development platform. It serves as the entry point for dependency injection
 * and global application configuration.
 * 
 * Features:
 * - Hilt dependency injection setup
 * - Global application state management
 * - Performance monitoring initialization
 * 
 * @author AIBuild Team
 * @version 1.0
 */
@HiltAndroidApp
class AIBuildApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize performance monitoring for development
        initializePerformanceMonitoring()
        
        // Initialize crash reporting
        initializeCrashReporting()
        
        // Setup global error handling
        setupGlobalErrorHandling()
    }
    
    /**
     * Initialize performance monitoring for tracking app performance
     */
    private fun initializePerformanceMonitoring() {
        // TODO: Implement performance monitoring
        // This will be integrated with the Monitoring & Diagnostics module
    }
    
    /**
     * Initialize crash reporting to track and analyze crashes
     */
    private fun initializeCrashReporting() {
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
    }
}