package com.msg91.chatwidget

import android.content.Context
import android.view.View

/**
 * Main entry point for the Chat Widget SDK
 * 
 * Provides a clean, simple API with only the essential methods:
 * - initialize(): Set up the chat widget with configuration
 * - update(): Update the widget configuration and reload
 */
interface ChatSDK {
    
    /**
     * Initialize the chat widget with configuration
     * @param context Android context
     * @param config Map containing widgetToken and optional parameters
     * @return View that can be added to any ViewGroup
     */
    fun initialize(context: Context, config: Map<String, Any>): View
    
    /**
     * Update widget configuration and reload content
     * @param config Updated configuration map
     */
    fun update(config: Map<String, Any>)
    
    /**
     * Check if the SDK is properly initialized
     */
    val isInitialized: Boolean
    
    companion object {
        /**
         * Create a new ChatSDK instance
         */
        fun create(): ChatSDK = ChatSDKImpl()
    }
}