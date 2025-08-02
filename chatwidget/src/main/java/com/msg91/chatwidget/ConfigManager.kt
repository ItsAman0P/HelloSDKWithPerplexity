package com.msg91.chatwidget

/**
 * Single Responsibility: Configuration validation and management
 * 
 * Handles all configuration-related operations including validation,
 * storage, and retrieval of widget configuration parameters.
 */
internal class ConfigManager {
    
    private val config = mutableMapOf<String, Any>()
    
    /**
     * Set and validate configuration
     */
    fun setConfig(newConfig: Map<String, Any>) {
        validateConfig(newConfig)
        config.clear()
        config.putAll(newConfig)
    }
    
    /**
     * Get current configuration
     */
    fun getConfig(): Map<String, Any> = config.toMap()
    
    /**
     * Get widget token (required field)
     */
    fun getWidgetToken(): String = config["widgetToken"] as String
    
    /**
     * Check if configuration is valid
     */
    fun isValid(): Boolean = config.containsKey("widgetToken")
    
    /**
     * Validate configuration parameters
     */
    private fun validateConfig(config: Map<String, Any>) {
        if (!config.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing required 'widgetToken' in configuration")
        }
        
        val widgetToken = config["widgetToken"]
        if (widgetToken !is String || widgetToken.isBlank()) {
            throw IllegalArgumentException("'widgetToken' must be a non-empty string")
        }
    }
}