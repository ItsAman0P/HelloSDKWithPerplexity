package com.msg91.chatwidget

/**
 * Simple internal logging utility
 * 
 * Provides basic logging functionality for the SDK.
 * Keeps their existing logging behavior.
 */
internal object Logger {
    
    private var debugEnabled = true
    
    /**
     * Enable or disable debug logging
     */
    fun enableDebug(enabled: Boolean) {
        debugEnabled = enabled
    }
    
    /**
     * Log debug message
     */
    fun debug(message: String) {
        if (debugEnabled) {
            println("[MSG91 HELLO SDK]: $message")
        }
    }
    
    /**
     * Log error message
     */
    fun error(message: String, throwable: Throwable? = null) {
        if (debugEnabled) {
            println("[MSG91 HELLO SDK ERROR]: $message")
            throwable?.printStackTrace()
        }
    }
    
    /**
     * Log warning message
     */
    fun warn(message: String) {
        if (debugEnabled) {
            println("[MSG91 HELLO SDK WARN]: $message")
        }
    }
}