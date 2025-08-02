package com.msg91.chatwidget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Chat Widget View Component
 * 
 * A simple FrameLayout wrapper that uses the ChatSDK internally.
 * Provides backward compatibility for View-based usage.
 */
class ChatWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val helloConfig: Map<String, Any>,
    private val widgetColor: String? = null,
    private val isCloseButtonVisible: Boolean = true,
    private val useKeyboardAvoidingView: Boolean = true
) : FrameLayout(context, attrs) {

    private val sdk = ChatSDK.create()

    init {
        // Prepare config with optional parameters
        val config = helloConfig.toMutableMap()
        if (widgetColor != null) config["widgetColor"] = widgetColor
        config["isCloseButtonVisible"] = isCloseButtonVisible
        config["useKeyboardAvoidingView"] = useKeyboardAvoidingView

        // Initialize SDK and add its view
        val chatView = sdk.initialize(context, config)
        addView(chatView)
    }

    /**
     * Update widget configuration
     */
    fun updateHelloConfig(newConfig: Map<String, Any>) {
        val config = newConfig.toMutableMap()
        if (widgetColor != null) config["widgetColor"] = widgetColor
        config["isCloseButtonVisible"] = isCloseButtonVisible
        config["useKeyboardAvoidingView"] = useKeyboardAvoidingView
        
        sdk.update(config)
    }

    /**
     * Reload widget content
     */
    fun loadWidget() {
        // Configuration is already loaded, just update with current config
        val config = helloConfig.toMutableMap()
        if (widgetColor != null) config["widgetColor"] = widgetColor
        config["isCloseButtonVisible"] = isCloseButtonVisible
        config["useKeyboardAvoidingView"] = useKeyboardAvoidingView
        
        sdk.update(config)
    }

    /**
     * Load HTML content directly
     */
    fun loadHtmlDirectly(html: String) {
        // For direct HTML loading, we'd need to extend the SDK
        // For now, just reload with current config
        loadWidget()
    }
}