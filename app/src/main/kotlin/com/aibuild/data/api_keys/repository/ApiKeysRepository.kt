package com.aibuild.data.api_keys.repository

import android.content.Context
import com.aibuild.BuildConfig
import com.aibuild.data.api_keys.database.ApiKeysDatabase
import com.aibuild.data.api_keys.entity.ApiKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ApiKeysRepository - Secure repository for API key management
 * 
 * This repository provides a secure abstraction layer for API key storage and retrieval.
 * It implements multiple security features including in-memory caching, encryption,
 * and developer mode enforcement.
 * 
 * Security Features:
 * - In-memory cache with automatic clearing
 * - Database access only when developer mode is enabled
 * - Secure key obfuscation for logging
 * - Thread-safe operations with mutex locks
 * - Automatic cache invalidation on updates
 * 
 * @author AIBuild Developer Agent
 */
@Singleton
class ApiKeysRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val mutex = Mutex()
    private var database: ApiKeysDatabase? = null
    private var isDeveloperModeEnabled = false
    
    // In-memory cache for performance and security
    private val _cachedApiKeys = MutableStateFlow<ApiKeys?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _lastError = MutableStateFlow<String?>(null)
    
    // Public read-only flows
    val cachedApiKeys: StateFlow<ApiKeys?> = _cachedApiKeys.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val lastError: StateFlow<String?> = _lastError.asStateFlow()
    
    /**
     * Initialize repository with developer mode status
     * Must be called before any other operations
     */
    suspend fun initialize(developerModeEnabled: Boolean) = mutex.withLock {
        try {
            isDeveloperModeEnabled = developerModeEnabled
            
            if (developerModeEnabled) {
                database = ApiKeysDatabase.getDatabase(context, true)
                loadKeysFromDatabase()
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("ApiKeysRepository", "Repository initialized with developer mode enabled")
                }
            } else {
                clearCache()
                database = null
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("ApiKeysRepository", "Repository initialized with developer mode disabled")
                }
            }
            
            _lastError.value = null
        } catch (e: Exception) {
            val errorMsg = "Failed to initialize API keys repository: ${e.message}"
            _lastError.value = errorMsg
            
            if (BuildConfig.DEBUG) {
                android.util.Log.e("ApiKeysRepository", errorMsg, e)
            }
        }
    }
    
    /**
     * Get API keys as Flow for reactive UI updates
     */
    fun getApiKeysFlow(): Flow<ApiKeys?> {
        return if (isDeveloperModeEnabled && database != null) {
            database!!.apiKeysDao().getApiKeysFlow()
        } else {
            _cachedApiKeys
        }
    }
    
    /**
     * Get cached API keys synchronously
     */
    fun getCachedApiKeys(): ApiKeys? {
        return _cachedApiKeys.value
    }
    
    /**
     * Load API keys from database and update cache
     */
    private suspend fun loadKeysFromDatabase() {
        try {
            _isLoading.value = true
            val keys = database?.apiKeysDao()?.getApiKeys()
            _cachedApiKeys.value = keys
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("ApiKeysRepository", "Keys loaded from database: ${keys?.toObfuscatedString() ?: "null"}")
            }
        } catch (e: Exception) {
            _lastError.value = "Failed to load API keys: ${e.message}"
            
            if (BuildConfig.DEBUG) {
                android.util.Log.e("ApiKeysRepository", "Error loading keys from database", e)
            }
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Save or update API keys
     */
    suspend fun saveApiKeys(apiKeys: ApiKeys): Result<Unit> = mutex.withLock {
        return try {
            if (!isDeveloperModeEnabled || database == null) {
                return Result.failure(SecurityException("Developer mode required for API key operations"))
            }
            
            _isLoading.value = true
            
            val updatedKeys = apiKeys.withUpdatedTimestamp()
            database!!.apiKeysDao().insertOrUpdateApiKeys(updatedKeys)
            _cachedApiKeys.value = updatedKeys
            _lastError.value = null
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("ApiKeysRepository", "API keys saved successfully: ${updatedKeys.toObfuscatedString()}")
            }
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            val errorMsg = "Failed to save API keys: ${e.message}"
            _lastError.value = errorMsg
            
            if (BuildConfig.DEBUG) {
                android.util.Log.e("ApiKeysRepository", errorMsg, e)
            }
            
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Update specific API key
     */
    suspend fun updateApiKey(provider: ApiKeyProvider, key: String?): Result<Unit> = mutex.withLock {
        return try {
            if (!isDeveloperModeEnabled || database == null) {
                return Result.failure(SecurityException("Developer mode required for API key operations"))
            }
            
            _isLoading.value = true
            
            val dao = database!!.apiKeysDao()
            when (provider) {
                ApiKeyProvider.GOOGLE -> dao.updateGoogleKey(key)
                ApiKeyProvider.OPENAI -> dao.updateOpenAiKey(key)
                ApiKeyProvider.ANTHROPIC -> dao.updateAnthropicKey(key)
                ApiKeyProvider.OPENROUTER -> dao.updateOpenRouterKey(key)
                ApiKeyProvider.OLLAMA -> dao.updateOllamaKey(key)
            }
            
            // Reload cache
            loadKeysFromDatabase()
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("ApiKeysRepository", "${provider.name} API key updated successfully")
            }
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            val errorMsg = "Failed to update ${provider.name} API key: ${e.message}"
            _lastError.value = errorMsg
            
            if (BuildConfig.DEBUG) {
                android.util.Log.e("ApiKeysRepository", errorMsg, e)
            }
            
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Get specific API key by provider
     */
    fun getApiKey(provider: ApiKeyProvider): String? {
        val keys = _cachedApiKeys.value ?: return null
        return when (provider) {
            ApiKeyProvider.GOOGLE -> keys.googleKey
            ApiKeyProvider.OPENAI -> keys.openAiKey
            ApiKeyProvider.ANTHROPIC -> keys.anthropicKey
            ApiKeyProvider.OPENROUTER -> keys.openRouterKey
            ApiKeyProvider.OLLAMA -> keys.ollamaKey
        }
    }
    
    /**
     * Check if any API keys are configured
     */
    fun hasAnyApiKeys(): Boolean {
        return _cachedApiKeys.value?.hasAnyKey() ?: false
    }
    
    /**
     * Get count of configured API keys
     */
    fun getConfiguredKeysCount(): Int {
        return _cachedApiKeys.value?.configuredKeysCount() ?: 0
    }
    
    /**
     * Delete all API keys (security reset)
     */
    suspend fun deleteAllApiKeys(): Result<Unit> = mutex.withLock {
        return try {
            if (!isDeveloperModeEnabled || database == null) {
                return Result.failure(SecurityException("Developer mode required for API key operations"))
            }
            
            _isLoading.value = true
            
            database!!.apiKeysDao().deleteApiKeys()
            _cachedApiKeys.value = null
            _lastError.value = null
            
            if (BuildConfig.DEBUG) {
                android.util.Log.d("ApiKeysRepository", "All API keys deleted successfully")
            }
            
            Result.success(Unit)
            
        } catch (e: Exception) {
            val errorMsg = "Failed to delete API keys: ${e.message}"
            _lastError.value = errorMsg
            
            if (BuildConfig.DEBUG) {
                android.util.Log.e("ApiKeysRepository", errorMsg, e)
            }
            
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Clear in-memory cache
     */
    fun clearCache() {
        _cachedApiKeys.value = null
        _lastError.value = null
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("ApiKeysRepository", "Cache cleared")
        }
    }
    
    /**
     * Check if database exists
     */
    fun databaseExists(): Boolean {
        return ApiKeysDatabase.databaseExists(context)
    }
    
    /**
     * Get last updated timestamp
     */
    suspend fun getLastUpdatedTimestamp(): Long? {
        return try {
            database?.apiKeysDao()?.getLastUpdatedTimestamp()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                android.util.Log.e("ApiKeysRepository", "Error getting last updated timestamp", e)
            }
            null
        }
    }
}

/**
 * Enum for API key providers
 */
enum class ApiKeyProvider(val displayName: String) {
    GOOGLE("Google"),
    OPENAI("OpenAI"),
    ANTHROPIC("Anthropic"),
    OPENROUTER("OpenRouter"),
    OLLAMA("Ollama")
}