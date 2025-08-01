package com.msg91.chatwidget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import com.msg91.chatwidget.service.HtmlBuilder
import com.msg91.chatwidget.utils.LogUtil
import com.msg91.chatwidget.webview.ChatWebViewManager

class ChatWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    helloConfig: Map<String, Any>,
    private val widgetColor: String? = null,
    private val isCloseButtonVisible: Boolean = true,
    private val useKeyboardAvoidingView: Boolean = true
) : FrameLayout(context, attrs) {

    private val helloConfig: MutableMap<String, Any> = helloConfig.toMutableMap()
    private var chatWidgetFragment: ChatWidgetFragment? = null
    private var isFragmentAdded = false

    init {
        validateConfig()
        // Try to setup with fragment first, fallback to direct WebView if not possible
        setupWithFragment()
    }

    private fun validateConfig() {
        if (!helloConfig.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing `widget_token` in helloConfig")
        }
    }

    /**
     * Setup with fragment for proper file upload handling
     */
    private fun setupWithFragment() {
        try {
            val fragmentManager = findFragmentManager()
            LogUtil.log("[ChatWidget] FragmentManager found: ${fragmentManager != null}")
            
            if (fragmentManager != null && !isFragmentAdded) {
                chatWidgetFragment = ChatWidgetFragment.newInstance(
                    helloConfig = helloConfig,
                    widgetColor = widgetColor,
                    isCloseButtonVisible = isCloseButtonVisible,
                    useKeyboardAvoidingView = useKeyboardAvoidingView
                )
                
                // Generate unique ID for this widget if it doesn't have one
                if (id == NO_ID) {
                    id = generateViewId()
                }
                
                LogUtil.log("[ChatWidget] Adding fragment with ID: $id")
                
                fragmentManager.beginTransaction()
                    .replace(id, chatWidgetFragment!!, "ChatWidget_$id")
                    .commitNowAllowingStateLoss()
                
                isFragmentAdded = true
                LogUtil.log("[ChatWidget] Successfully setup with fragment for proper file upload")
            } else {
                LogUtil.log("[ChatWidget] No FragmentManager available, using direct WebView")
                // Fallback to direct WebView setup
                setupWithDirectWebView()
            }
        } catch (e: Exception) {
            LogUtil.log("[ChatWidget] Failed to setup with fragment: ${e.message}, falling back to direct WebView")
            e.printStackTrace()
            setupWithDirectWebView()
        }
    }

    private var fallbackWebViewManager: ChatWebViewManager? = null

    /**
     * Fallback setup with direct WebView (file upload may not work properly)
     */
    private fun setupWithDirectWebView() {
        LogUtil.log("[ChatWidget] Setting up with direct WebView - file upload may be limited")
        
        // WebView manager to handle all WebView-related functionality
        fallbackWebViewManager = ChatWebViewManager(
            context = context,
            fragment = null, // No fragment available
            filePickerLauncher = null, // No pre-registered launcher for fallback
            onReload = { loadHtmlContentDirect() },
            onClose = { /* Handle close if needed */ }
        )
        
        // Add the webview container to this frame layout
        addView(fallbackWebViewManager!!.getContainer())
        loadHtmlContentDirect()
        setupKeyboardAnimation()
    }

    /**
     * Find FragmentManager from context
     */
    private fun findFragmentManager(): androidx.fragment.app.FragmentManager? {
        var ctx = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is FragmentActivity) {
                return ctx.supportFragmentManager
            }
            ctx = ctx.baseContext
        }
        return null
    }

    /**
     * Direct HTML loading for fallback mode
     */
    private fun loadHtmlContentDirect() {
        val html = HtmlBuilder.buildWebViewHtml(
            helloConfig = helloConfig,
            widgetColor = widgetColor,
            isCloseButtonVisible = isCloseButtonVisible
        )
        fallbackWebViewManager?.loadHtmlContent(html)
        LogUtil.log("[ChatWidget] Direct HTML loading executed")
    }

    fun updateHelloConfig(newHelloConfig: Map<String, Any>) {
        if (!newHelloConfig.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing `widgetToken` in updated helloConfig")
        }

        helloConfig.clear()
        helloConfig.putAll(newHelloConfig)
        
        // Update fragment if available, otherwise fallback
        if (isFragmentAdded && chatWidgetFragment != null) {
            chatWidgetFragment?.updateHelloConfig(newHelloConfig)
        } else {
            loadHtmlContentDirect()
        }
    }

    private fun setupKeyboardAnimation() {
        ViewCompat.setWindowInsetsAnimationCallback(this,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    this@ChatWidget.translationY = -imeHeight.toFloat()
                    return insets
                }
            }
        )
    }

    fun loadWidget() {
        if (isFragmentAdded && chatWidgetFragment != null) {
            chatWidgetFragment?.loadWidget()
        } else {
            loadHtmlContentDirect()
        }
    }

    fun loadHtmlDirectly(html: String) {
        if (isFragmentAdded && chatWidgetFragment != null) {
            chatWidgetFragment?.loadHtmlDirectly(html)
        } else {
            fallbackWebViewManager?.loadHtmlContent(html)
            LogUtil.log("[ChatWidget] Direct HTML loaded via fallback WebView")
        }
    }
    
    /**
     * Clean up resources when the widget is destroyed
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            if (isFragmentAdded && chatWidgetFragment != null) {
                // Fragment cleanup is handled by the fragment manager
                LogUtil.log("[ChatWidget] Fragment-based widget detached")
            } else {
                fallbackWebViewManager?.cleanup()
                LogUtil.log("[ChatWidget] Fallback WebView cleaned up")
            }
            LogUtil.log("[ChatWidget] Widget detached and cleaned up")
        } catch (e: Exception) {
            LogUtil.log("[ChatWidget] Error during cleanup: ${e.message}")
        }
    }
}

