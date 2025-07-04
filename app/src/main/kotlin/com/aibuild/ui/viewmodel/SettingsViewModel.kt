package com.aibuild.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aibuild.BuildConfig
import com.aibuild.data.api_keys.entity.ApiKeys
import com.aibuild.data.api_keys.repository.ApiKeyProvider
import com.aibuild.data.api_keys.repository.ApiKeysRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel - Manages settings state and API key operations
 * 
 * This ViewModel provides a secure interface for managing app settings and API keys.
 * It implements proper MVVM architecture with reactive state management and
 * comprehensive error handling.
 * 
 * Features:
 * - Reactive state management with StateFlow
 * - Secure API key operations through repository
 * - Developer mode enforcement
 * - Comprehensive error handling and user feedback
 * - Loading states for better UX
 * 
 * @author AIBuild Developer Agent
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiKeysRepository: ApiKeysRepository
) : ViewModel() {

    // UI State for general settings
    private val _darkModeEnabled = MutableStateFlow(false)
    private val _notificationsEnabled = MutableStateFlow(true)
    private val _autoSaveEnabled = MutableStateFlow(true)
    private val _debugModeEnabled = MutableStateFlow(false)
    private val _showPerformanceOverlay = MutableStateFlow(false)
    private val _enableBetaFeatures = MutableStateFlow(false)
    private val _verboseResponses = MutableStateFlow(false)
    private val _autoSuggestionsEnabled = MutableStateFlow(true)
    private val _multiAgentMode = MutableStateFlow(false)

    // UI State for API keys
    private val _isApiKeysLoading = MutableStateFlow(false)
    private val _apiKeysError = MutableStateFlow<String?>(null)
    private val _apiKeysSuccess = MutableStateFlow<String?>(null)
    private val _showApiKeyDialog = MutableStateFlow<ApiKeyProvider?>(null)

    // API key input states
    private val _googleKeyInput = MutableStateFlow("")
    private val _openAiKeyInput = MutableStateFlow("")
    private val _anthropicKeyInput = MutableStateFlow("")
    private val _openRouterKeyInput = MutableStateFlow("")
    private val _ollamaKeyInput = MutableStateFlow("")

    // Expose read-only state flows
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()
    val autoSaveEnabled: StateFlow<Boolean> = _autoSaveEnabled.asStateFlow()
    val debugModeEnabled: StateFlow<Boolean> = _debugModeEnabled.asStateFlow()
    val showPerformanceOverlay: StateFlow<Boolean> = _showPerformanceOverlay.asStateFlow()
    val enableBetaFeatures: StateFlow<Boolean> = _enableBetaFeatures.asStateFlow()
    val verboseResponses: StateFlow<Boolean> = _verboseResponses.asStateFlow()
    val autoSuggestionsEnabled: StateFlow<Boolean> = _autoSuggestionsEnabled.asStateFlow()
    val multiAgentMode: StateFlow<Boolean> = _multiAgentMode.asStateFlow()

    val isApiKeysLoading: StateFlow<Boolean> = _isApiKeysLoading.asStateFlow()
    val apiKeysError: StateFlow<String?> = _apiKeysError.asStateFlow()
    val apiKeysSuccess: StateFlow<String?> = _apiKeysSuccess.asStateFlow()
    val showApiKeyDialog: StateFlow<ApiKeyProvider?> = _showApiKeyDialog.asStateFlow()

    val googleKeyInput: StateFlow<String> = _googleKeyInput.asStateFlow()
    val openAiKeyInput: StateFlow<String> = _openAiKeyInput.asStateFlow()
    val anthropicKeyInput: StateFlow<String> = _anthropicKeyInput.asStateFlow()
    val openRouterKeyInput: StateFlow<String> = _openRouterKeyInput.asStateFlow()
    val ollamaKeyInput: StateFlow<String> = _ollamaKeyInput.asStateFlow()

    // Combined state for API keys
    val apiKeysState: StateFlow<ApiKeys?> = apiKeysRepository.cachedApiKeys
    val repositoryLoading: StateFlow<Boolean> = apiKeysRepository.isLoading
    val repositoryError: StateFlow<String?> = apiKeysRepository.lastError

    init {
        initializeRepository()
        observeApiKeysChanges()
    }

    /**
     * Initialize repository with current developer mode status
     */
    private fun initializeRepository() {
        viewModelScope.launch {
            try {
                // Initialize with current debug mode status
                apiKeysRepository.initialize(_debugModeEnabled.value)
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("SettingsViewModel", "Repository initialized")
                }
            } catch (e: Exception) {
                _apiKeysError.value = "Failed to initialize secure storage: ${e.message}"
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("SettingsViewModel", "Failed to initialize repository", e)
                }
            }
        }
    }

    /**
     * Observe API keys changes and update input fields
     */
    private fun observeApiKeysChanges() {
        viewModelScope.launch {
            apiKeysState.collect { apiKeys ->
                if (apiKeys != null) {
                    // Update input fields with current values (for display only)
                    _googleKeyInput.value = apiKeys.googleKey?.let { obfuscateKey(it) } ?: ""
                    _openAiKeyInput.value = apiKeys.openAiKey?.let { obfuscateKey(it) } ?: ""
                    _anthropicKeyInput.value = apiKeys.anthropicKey?.let { obfuscateKey(it) } ?: ""
                    _openRouterKeyInput.value = apiKeys.openRouterKey?.let { obfuscateKey(it) } ?: ""
                    _ollamaKeyInput.value = apiKeys.ollamaKey?.let { obfuscateKey(it) } ?: ""
                }
            }
        }
    }

    // Settings actions
    fun toggleDarkMode() {
        _darkModeEnabled.value = !_darkModeEnabled.value
    }

    fun toggleNotifications() {
        _notificationsEnabled.value = !_notificationsEnabled.value
    }

    fun toggleAutoSave() {
        _autoSaveEnabled.value = !_autoSaveEnabled.value
    }

    fun toggleDebugMode() {
        val newValue = !_debugModeEnabled.value
        _debugModeEnabled.value = newValue
        
        // Reinitialize repository when debug mode changes
        viewModelScope.launch {
            apiKeysRepository.initialize(newValue)
        }
    }

    fun togglePerformanceOverlay() {
        _showPerformanceOverlay.value = !_showPerformanceOverlay.value
    }

    fun toggleBetaFeatures() {
        _enableBetaFeatures.value = !_enableBetaFeatures.value
    }

    fun toggleVerboseResponses() {
        _verboseResponses.value = !_verboseResponses.value
    }

    fun toggleAutoSuggestions() {
        _autoSuggestionsEnabled.value = !_autoSuggestionsEnabled.value
    }

    fun toggleMultiAgentMode() {
        _multiAgentMode.value = !_multiAgentMode.value
    }

    // API Key actions
    fun showApiKeyDialog(provider: ApiKeyProvider) {
        _showApiKeyDialog.value = provider
        clearMessages()
    }

    fun hideApiKeyDialog() {
        _showApiKeyDialog.value = null
        clearMessages()
    }

    fun updateApiKeyInput(provider: ApiKeyProvider, value: String) {
        when (provider) {
            ApiKeyProvider.GOOGLE -> _googleKeyInput.value = value
            ApiKeyProvider.OPENAI -> _openAiKeyInput.value = value
            ApiKeyProvider.ANTHROPIC -> _anthropicKeyInput.value = value
            ApiKeyProvider.OPENROUTER -> _openRouterKeyInput.value = value
            ApiKeyProvider.OLLAMA -> _ollamaKeyInput.value = value
        }
    }

    fun saveApiKey(provider: ApiKeyProvider, key: String) {
        if (!_debugModeEnabled.value) {
            _apiKeysError.value = "Developer mode must be enabled to manage API keys"
            return
        }

        viewModelScope.launch {
            try {
                _isApiKeysLoading.value = true
                clearMessages()

                val trimmedKey = key.trim().takeIf { it.isNotBlank() }
                val result = apiKeysRepository.updateApiKey(provider, trimmedKey)

                if (result.isSuccess) {
                    _apiKeysSuccess.value = "${provider.displayName} API key saved successfully"
                    hideApiKeyDialog()
                    
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("SettingsViewModel", "${provider.displayName} API key saved")
                    }
                } else {
                    _apiKeysError.value = result.exceptionOrNull()?.message ?: "Failed to save API key"
                }

            } catch (e: Exception) {
                _apiKeysError.value = "Error saving API key: ${e.message}"
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("SettingsViewModel", "Error saving API key", e)
                }
            } finally {
                _isApiKeysLoading.value = false
            }
        }
    }

    fun deleteApiKey(provider: ApiKeyProvider) {
        if (!_debugModeEnabled.value) {
            _apiKeysError.value = "Developer mode must be enabled to manage API keys"
            return
        }

        viewModelScope.launch {
            try {
                _isApiKeysLoading.value = true
                clearMessages()

                val result = apiKeysRepository.updateApiKey(provider, null)

                if (result.isSuccess) {
                    _apiKeysSuccess.value = "${provider.displayName} API key deleted successfully"
                    
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("SettingsViewModel", "${provider.displayName} API key deleted")
                    }
                } else {
                    _apiKeysError.value = result.exceptionOrNull()?.message ?: "Failed to delete API key"
                }

            } catch (e: Exception) {
                _apiKeysError.value = "Error deleting API key: ${e.message}"
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("SettingsViewModel", "Error deleting API key", e)
                }
            } finally {
                _isApiKeysLoading.value = false
            }
        }
    }

    fun deleteAllApiKeys() {
        if (!_debugModeEnabled.value) {
            _apiKeysError.value = "Developer mode must be enabled to manage API keys"
            return
        }

        viewModelScope.launch {
            try {
                _isApiKeysLoading.value = true
                clearMessages()

                val result = apiKeysRepository.deleteAllApiKeys()

                if (result.isSuccess) {
                    _apiKeysSuccess.value = "All API keys deleted successfully"
                    
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("SettingsViewModel", "All API keys deleted")
                    }
                } else {
                    _apiKeysError.value = result.exceptionOrNull()?.message ?: "Failed to delete all API keys"
                }

            } catch (e: Exception) {
                _apiKeysError.value = "Error deleting all API keys: ${e.message}"
                
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("SettingsViewModel", "Error deleting all API keys", e)
                }
            } finally {
                _isApiKeysLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _apiKeysError.value = null
        _apiKeysSuccess.value = null
    }

    fun getApiKeyDisplayValue(provider: ApiKeyProvider): String {
        val key = apiKeysRepository.getApiKey(provider)
        return if (key.isNullOrBlank()) {
            "Not configured"
        } else {
            obfuscateKey(key)
        }
    }

    fun hasAnyApiKeys(): Boolean {
        return apiKeysRepository.hasAnyApiKeys()
    }

    fun getConfiguredKeysCount(): Int {
        return apiKeysRepository.getConfiguredKeysCount()
    }

    /**
     * Obfuscate API key for display (security)
     */
    private fun obfuscateKey(key: String): String {
        return if (key.length <= 8) {
            "••••••••"
        } else {
            "••••••••${key.takeLast(4)}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clear any sensitive data from memory
        apiKeysRepository.clearCache()
        
        if (BuildConfig.DEBUG) {
            android.util.Log.d("SettingsViewModel", "ViewModel cleared and cache cleaned")
        }
    }
}