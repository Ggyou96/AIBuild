package com.aibuild.data.api_keys.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ApiKeys Entity - Stores encrypted API keys for various AI services
 * 
 * This entity represents the secure storage structure for API keys used
 * throughout the AIBuild platform. All keys are encrypted before storage
 * and decrypted only when needed for API calls.
 * 
 * Security Features:
 * - Room database with encryption
 * - Never logged or sent to analytics
 * - Loaded only when Developer Mode is enabled
 * - Optional biometric protection for editing
 * 
 * @author AIBuild Developer Agent
 */
@Entity(tableName = "api_keys")
data class ApiKeys(
    @PrimaryKey
    val id: Int = 1, // Single row for all keys
    
    @ColumnInfo(name = "google_key")
    val googleKey: String? = null,
    
    @ColumnInfo(name = "openai_key") 
    val openAiKey: String? = null,
    
    @ColumnInfo(name = "anthropic_key")
    val anthropicKey: String? = null,
    
    @ColumnInfo(name = "openrouter_key")
    val openRouterKey: String? = null,
    
    @ColumnInfo(name = "ollama_key")
    val ollamaKey: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns a copy with updated timestamp
     */
    fun withUpdatedTimestamp(): ApiKeys {
        return copy(updatedAt = System.currentTimeMillis())
    }
    
    /**
     * Checks if any API key is configured
     */
    fun hasAnyKey(): Boolean {
        return !googleKey.isNullOrBlank() ||
               !openAiKey.isNullOrBlank() ||
               !anthropicKey.isNullOrBlank() ||
               !openRouterKey.isNullOrBlank() ||
               !ollamaKey.isNullOrBlank()
    }
    
    /**
     * Returns count of configured keys
     */
    fun configuredKeysCount(): Int {
        var count = 0
        if (!googleKey.isNullOrBlank()) count++
        if (!openAiKey.isNullOrBlank()) count++
        if (!anthropicKey.isNullOrBlank()) count++
        if (!openRouterKey.isNullOrBlank()) count++
        if (!ollamaKey.isNullOrBlank()) count++
        return count
    }
    
    /**
     * Returns obfuscated version for logging (security)
     */
    fun toObfuscatedString(): String {
        return "ApiKeys(id=$id, " +
               "googleKey=${googleKey?.let { "***${it.takeLast(4)}" } ?: "null"}, " +
               "openAiKey=${openAiKey?.let { "***${it.takeLast(4)}" } ?: "null"}, " +
               "anthropicKey=${anthropicKey?.let { "***${it.takeLast(4)}" } ?: "null"}, " +
               "openRouterKey=${openRouterKey?.let { "***${it.takeLast(4)}" } ?: "null"}, " +
               "ollamaKey=${ollamaKey?.let { "***${it.takeLast(4)}" } ?: "null"}, " +
               "configuredKeys=${configuredKeysCount()})"
    }
}