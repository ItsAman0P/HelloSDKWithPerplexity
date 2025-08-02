package com.msg91.chatwidget.utils

import com.msg91.chatwidget.Logger

/**
 * Legacy LogUtil that delegates to new Logger
 * 
 * Maintains backward compatibility while using the new logging system internally.
 */
object LogUtil {
    
    /**
     * Log message - delegates to new Logger
     */
    fun log(vararg messages: Any?) {
        val output = messages.joinToString(" ") { it.toString() }
        Logger.debug(output)
    }
    
    /**
     * Enable/disable debug logging
     */
    fun setDebugEnabled(enabled: Boolean) {
        Logger.enableDebug(enabled)
    }
}