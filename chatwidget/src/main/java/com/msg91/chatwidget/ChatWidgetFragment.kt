package com.msg91.chatwidget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.msg91.chatwidget.service.HtmlBuilder
import com.msg91.chatwidget.utils.LogUtil
import com.msg91.chatwidget.webview.ChatWebViewManager

/**
 * Fragment-based ChatWidget that properly handles file uploads and downloads
 * This fragment is designed to be used internally by ChatWidget or directly by consumers
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

    private lateinit var helloConfig: MutableMap<String, Any>
    private var widgetColor: String? = null
    private var isCloseButtonVisible: Boolean = true
    private var useKeyboardAvoidingView: Boolean = true

    private lateinit var webViewManager: ChatWebViewManager
    private lateinit var container: FrameLayout
    
    // Pre-register ActivityResultLauncher for file uploads
    private val filePickerLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Delegate result to WebViewManager when it's ready
        if (::webViewManager.isInitialized) {
            webViewManager.handleFilePickerResult(result.resultCode, result.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            helloConfig = (args.getSerializable(ARG_HELLO_CONFIG) as? HashMap<String, Any>)?.toMutableMap()
                ?: mutableMapOf()
            widgetColor = args.getString(ARG_WIDGET_COLOR)
            isCloseButtonVisible = args.getBoolean(ARG_CLOSE_BUTTON_VISIBLE, true)
            useKeyboardAvoidingView = args.getBoolean(ARG_KEYBOARD_AVOIDING, true)
        }
        
        validateConfig()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create the main container programmatically
        this.container = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        return this.container
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        LogUtil.log("[ChatWidgetFragment] onViewCreated started")
        LogUtil.log("[ChatWidgetFragment] Config: $helloConfig")
        
        // Initialize WebView manager with fragment context for proper Activity Result API
        webViewManager = ChatWebViewManager(
            context = requireContext(),
            fragment = this, // Pass fragment for proper file upload handling
            filePickerLauncher = filePickerLauncher, // Pass pre-registered launcher
            onClose = { handleClose() }
        )
        
        // Add the webview container to our fragment's container
        container.addView(webViewManager.getContainer())
        LogUtil.log("[ChatWidgetFragment] WebView container added")
        
        // Setup keyboard handling if enabled
        if (useKeyboardAvoidingView) {
            setupKeyboardAnimation()
        }
        
        // Setup system bar insets
        // setupSystemBarInsets()
        
        // Load the initial content
        loadHtmlContent()
        
        LogUtil.log("[ChatWidgetFragment] Fragment initialized successfully")
    }

    private fun validateConfig() {
        if (!helloConfig.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing 'widgetToken' in helloConfig")
        }
    }

    private fun loadHtmlContent() {
        if (::webViewManager.isInitialized) {
            val html = HtmlBuilder.buildWebViewHtml(
                helloConfig = helloConfig,
                widgetColor = widgetColor,
                isCloseButtonVisible = isCloseButtonVisible
            )
            webViewManager.loadHtmlContent(html)
            LogUtil.log("[ChatWidgetFragment] HTML content loaded")
        }


    }

    private fun setupKeyboardAnimation() {
        ViewCompat.setWindowInsetsAnimationCallback(
            container,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    container.translationY = -imeHeight.toFloat()
                    return insets
                }
            }
        )
    }

    private fun setupSystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            // Apply left/right/bottom padding only (top is handled by parent activity)
            // Bottom only if keyboard is NOT visible
            view.setPadding(
                systemBars.left,
                0, // Don't apply top padding - handled by parent
                systemBars.right,
                // if (isImeVisible) 0 else systemBars.bottom
                0
            )

            insets
        }
    }

    private fun handleClose() {
        // Handle close action - could remove fragment or notify parent
        parentFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    /**
     * Update the widget configuration and reload content
     */
    fun updateHelloConfig(newHelloConfig: Map<String, Any>) {
        if (!newHelloConfig.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing 'widgetToken' in updated helloConfig")
        }

        helloConfig.clear()
        helloConfig.putAll(newHelloConfig)
        loadHtmlContent()
        LogUtil.log("[ChatWidgetFragment] Configuration updated")
    }

    /**
     * Reload the widget content
     */
    fun loadWidget() {
        loadHtmlContent()
    }

    /**
     * Load HTML content directly
     */
    fun loadHtmlDirectly(html: String) {
        if (::webViewManager.isInitialized) {
            webViewManager.loadHtmlContent(html)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (::webViewManager.isInitialized) {
                webViewManager.cleanup()
            }
            LogUtil.log("[ChatWidgetFragment] Fragment destroyed and cleaned up")
        } catch (e: Exception) {
            LogUtil.log("[ChatWidgetFragment] Error during cleanup: ${e.message}")
        }
    }
}