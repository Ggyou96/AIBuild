package com.aibuild.data.api_keys.dao

import androidx.room.*
import com.aibuild.data.api_keys.entity.ApiKeys
import kotlinx.coroutines.flow.Flow

/**
 * ApiKeysDao - Data Access Object for API keys operations
 * 
 * Provides secure database operations for API key storage and retrieval.
 * All operations are designed with security in mind and support reactive
 * updates through Kotlin Flow.
 * 
 * Security Considerations:
 * - All queries return encrypted data that must be decrypted in repository
 * - No direct access to raw API keys in logs
 * - Supports atomic updates to prevent partial key corruption
 * 
 * @author AIBuild Developer Agent
 */
@Dao
interface ApiKeysDao {
    
    /**
     * Get API keys as Flow for reactive updates
     * Returns null if no keys are stored
     */
    @Query("SELECT * FROM api_keys WHERE id = 1")
    fun getApiKeysFlow(): Flow<ApiKeys?>
    
    /**
     * Get API keys synchronously
     * Used for one-time operations and initialization
     */
    @Query("SELECT * FROM api_keys WHERE id = 1")
    suspend fun getApiKeys(): ApiKeys?
    
    /**
     * Insert or replace all API keys
     * This is an atomic operation to ensure data consistency
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApiKeys(apiKeys: ApiKeys)
    
    /**
     * Update specific Google API key
     */
    @Query("UPDATE api_keys SET google_key = :googleKey, updated_at = :timestamp WHERE id = 1")
    suspend fun updateGoogleKey(googleKey: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Update specific OpenAI API key
     */
    @Query("UPDATE api_keys SET openai_key = :openAiKey, updated_at = :timestamp WHERE id = 1")
    suspend fun updateOpenAiKey(openAiKey: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Update specific Anthropic API key
     */
    @Query("UPDATE api_keys SET anthropic_key = :anthropicKey, updated_at = :timestamp WHERE id = 1")
    suspend fun updateAnthropicKey(anthropicKey: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Update specific OpenRouter API key
     */
    @Query("UPDATE api_keys SET openrouter_key = :openRouterKey, updated_at = :timestamp WHERE id = 1")
    suspend fun updateOpenRouterKey(openRouterKey: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Update specific Ollama API key
     */
    @Query("UPDATE api_keys SET ollama_key = :ollamaKey, updated_at = :timestamp WHERE id = 1")
    suspend fun updateOllamaKey(ollamaKey: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * Check if API keys exist
     */
    @Query("SELECT COUNT(*) FROM api_keys WHERE id = 1")
    suspend fun hasApiKeys(): Boolean
    
    /**
     * Delete all API keys (for security reset)
     */
    @Query("DELETE FROM api_keys WHERE id = 1")
    suspend fun deleteApiKeys()
    
    /**
     * Get last updated timestamp
     */
    @Query("SELECT updated_at FROM api_keys WHERE id = 1")
    suspend fun getLastUpdatedTimestamp(): Long?
    
    /**
     * Batch update multiple keys atomically
     * Ensures either all keys are updated or none
     */
    @Transaction
    suspend fun updateMultipleKeys(
        googleKey: String? = null,
        openAiKey: String? = null,
        anthropicKey: String? = null,
        openRouterKey: String? = null,
        ollamaKey: String? = null
    ) {
        val timestamp = System.currentTimeMillis()
        val currentKeys = getApiKeys()
        
        if (currentKeys == null) {
            // Create new entry
            insertOrUpdateApiKeys(
                ApiKeys(
                    googleKey = googleKey,
                    openAiKey = openAiKey,
                    anthropicKey = anthropicKey,
                    openRouterKey = openRouterKey,
                    ollamaKey = ollamaKey,
                    updatedAt = timestamp
                )
            )
        } else {
            // Update existing entry
            insertOrUpdateApiKeys(
                currentKeys.copy(
                    googleKey = googleKey ?: currentKeys.googleKey,
                    openAiKey = openAiKey ?: currentKeys.openAiKey,
                    anthropicKey = anthropicKey ?: currentKeys.anthropicKey,
                    openRouterKey = openRouterKey ?: currentKeys.openRouterKey,
                    ollamaKey = ollamaKey ?: currentKeys.ollamaKey,
                    updatedAt = timestamp
                )
            )
        }
    }
}