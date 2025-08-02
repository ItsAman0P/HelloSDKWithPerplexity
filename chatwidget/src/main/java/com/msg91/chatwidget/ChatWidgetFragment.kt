package com.msg91.chatwidget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

/**
 * Chat Widget Fragment Component
 * 
 * A simple Fragment wrapper that uses the ChatSDK internally.
 * Provides backward compatibility for Fragment-based usage.
 */
class ChatWidgetFragment : Fragment() {

    companion object {
        private const val ARG_HELLO_CONFIG = "helloConfig"
        private const val ARG_WIDGET_COLOR = "widgetColor"
        private const val ARG_CLOSE_BUTTON_VISIBLE = "isCloseButtonVisible"
        private const val ARG_KEYBOARD_AVOIDING = "useKeyboardAvoidingView"

        fun newInstance(
            helloConfig: Map<String, Any>,
            widgetColor: String? = null,
            isCloseButtonVisible: Boolean = true,
            useKeyboardAvoidingView: Boolean = true
        ): ChatWidgetFragment {
            val fragment = ChatWidgetFragment()
            val args = Bundle().apply {
                putSerializable(ARG_HELLO_CONFIG, HashMap(helloConfig))
                putString(ARG_WIDGET_COLOR, widgetColor)
                putBoolean(ARG_CLOSE_BUTTON_VISIBLE, isCloseButtonVisible)
                putBoolean(ARG_KEYBOARD_AVOIDING, useKeyboardAvoidingView)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var sdk: ChatSDK
    private lateinit var container: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create container
        this.container = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return this.container
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Extract arguments
        val args = arguments ?: throw IllegalArgumentException("Fragment arguments required")
        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        val helloConfig = (args.getSerializable(ARG_HELLO_CONFIG) as? HashMap<String, Any>)
            ?: throw IllegalArgumentException("helloConfig required")
        val widgetColor = args.getString(ARG_WIDGET_COLOR)
        val isCloseButtonVisible = args.getBoolean(ARG_CLOSE_BUTTON_VISIBLE, true)
        val useKeyboardAvoidingView = args.getBoolean(ARG_KEYBOARD_AVOIDING, true)

        // Prepare config
        val config = helloConfig.toMutableMap()
        if (widgetColor != null) config["widgetColor"] = widgetColor
        config["isCloseButtonVisible"] = isCloseButtonVisible
        config["useKeyboardAvoidingView"] = useKeyboardAvoidingView

        // Initialize SDK
        sdk = ChatSDK.create()
        val chatView = sdk.initialize(requireContext(), config)
        container.addView(chatView)
    }

    /**
     * Update widget configuration
     */
    fun updateHelloConfig(newHelloConfig: Map<String, Any>) {
        if (::sdk.isInitialized) {
            val args = arguments
            val widgetColor = args?.getString(ARG_WIDGET_COLOR)
            val isCloseButtonVisible = args?.getBoolean(ARG_CLOSE_BUTTON_VISIBLE, true) ?: true
            val useKeyboardAvoidingView = args?.getBoolean(ARG_KEYBOARD_AVOIDING, true) ?: true

            val config = newHelloConfig.toMutableMap()
            if (widgetColor != null) config["widgetColor"] = widgetColor
            config["isCloseButtonVisible"] = isCloseButtonVisible
            config["useKeyboardAvoidingView"] = useKeyboardAvoidingView

            sdk.update(config)
        }
    }

    /**
     * Reload widget content
     */
    fun loadWidget() {
        if (::sdk.isInitialized) {
            // Get current config and reload
            val args = arguments ?: return
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            val helloConfig = (args.getSerializable(ARG_HELLO_CONFIG) as? HashMap<String, Any>) ?: return
            
            updateHelloConfig(helloConfig)
        }
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