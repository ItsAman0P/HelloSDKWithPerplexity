package com.msg91.chatwidget

import android.content.Context
import android.view.View
import android.widget.FrameLayout

/**
 * Main implementation of ChatSDK interface
 * 
 * Orchestrates all components following SOLID principles:
 * - Single Responsibility: Each component has one job
 * - Open/Closed: Easy to extend without modifying existing code
 * - Dependency Inversion: Depends on abstractions, not concretions
 */
internal class ChatSDKImpl : ChatSDK {
    
    private var webViewManager: WebViewManager? = null
    private var configManager: ConfigManager? = null
    private var htmlGenerator: HtmlGenerator? = null
    private var container: FrameLayout? = null
    private var context: Context? = null
    
    override val isInitialized: Boolean
        get() = webViewManager != null && configManager != null
    
    /**
     * Initialize the chat widget
     */
    override fun initialize(context: Context, config: Map<String, Any>): View {
        Logger.debug("Initializing ChatSDK with config: $config")
        
        try {
            this.context = context
            
            // Initialize configuration manager
            configManager = ConfigManager().apply {
                setConfig(config)
            }
            
            // Initialize HTML generator
            htmlGenerator = HtmlGenerator()
            
            // Initialize WebView manager
            webViewManager = WebViewManager(context)
            
            // Setup WebView
            setupWebView(context)
            
            Logger.debug("ChatSDK initialized successfully")
            return container!!
            
        } catch (e: Exception) {
            Logger.error("Failed to initialize ChatSDK", e)
            throw e
        }
    }
    
    /**
     * Update configuration and reload
     */
    override fun update(config: Map<String, Any>) {
        Logger.debug("Updating ChatSDK configuration: $config")
        
        if (!isInitialized) {
            throw IllegalStateException("ChatSDK must be initialized before calling update()")
        }
        
        try {
            // Update configuration
            configManager!!.setConfig(config)
            
            // Generate new HTML with updated config
            val html = generateHtml(config)
            
            // Load new content
            webViewManager!!.loadContent(html)
            
            Logger.debug("ChatSDK configuration updated successfully")
            
        } catch (e: Exception) {
            Logger.error("Failed to update ChatSDK configuration", e)
            throw e
        }
    }
    
    /**
     * Clean up resources (internal method)
     */
    fun release() {
        try {
            webViewManager?.release()
            webViewManager = null
            configManager = null
            htmlGenerator = null
            container = null
            context = null
            
            Logger.debug("ChatSDK released successfully")
            
        } catch (e: Exception) {
            Logger.error("Error during ChatSDK release", e)
        }
    }
    
    /**
     * Setup WebView and load initial content
     */
    private fun setupWebView(context: Context) {
        // Create WebView
        val webView = webViewManager!!.createWebView()
        container = webViewManager!!.getContainer()
        
        // Generate and load HTML
        val currentConfig = configManager!!.getConfig()
        val html = generateHtml(currentConfig)
        webViewManager!!.loadContent(html)
        
        Logger.debug("WebView setup completed")
    }
    
    /**
     * Generate HTML using current configuration
     */
    private fun generateHtml(config: Map<String, Any>): String {
        return htmlGenerator!!.generateHtml(config)
    }
}