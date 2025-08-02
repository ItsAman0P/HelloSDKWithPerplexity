package com.msg91.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import android.content.Intent
import com.msg91.chatwidget.webview.CustomWebChromeClient
import com.msg91.chatwidget.webview.CustomWebViewClient
import com.msg91.chatwidget.webview.WebAppInterface

/**
 * Single Responsibility: WebView lifecycle and configuration management
 * 
 * Manages WebView creation, setup, content loading, and cleanup.
 * Uses their existing WebView configuration and components.
 */
internal class WebViewManager(private val context: Context) {
    
    private var webView: WebView? = null
    private var container: FrameLayout? = null
    private var fileUploadHandler: FileUploadHandler? = null
    
    companion object {
        private const val JAVASCRIPT_INTERFACE = "ReactNativeWebView"
    }
    
    /**
     * Create and configure WebView with their existing settings
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun createWebView(): WebView {
        if (webView != null) return webView!!
        
        webView = WebView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        
        configureWebViewSettings(webView!!)
        setupWebViewClients(webView!!)
        
        // Create container
        container = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            addView(webView)
            setBackgroundColor(Color.TRANSPARENT)
        }
        
        return webView!!
    }
    
    /**
     * Get the container view
     */
    fun getContainer(): FrameLayout = container ?: throw IllegalStateException("WebView not created")
    
    /**
     * Load HTML content
     */
    fun loadContent(html: String) {
        webView?.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }
    
    /**
     * Reload current content
     */
    fun reload() {
        webView?.reload()
    }
    
    /**
     * Clean up resources
     */
    fun release() {
        try {
            fileUploadHandler?.release()
            webView?.destroy()
            webView = null
            container = null
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
    
    /**
     * Configure WebView settings using their existing configuration
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebViewSettings(webView: WebView) {
        // Enable WebView debugging
        WebView.setWebContentsDebuggingEnabled(true)
        
        webView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            setSupportZoom(false)
            builtInZoomControls = false
            mediaPlaybackRequiresUserGesture = false
            setGeolocationEnabled(true)
            cacheMode = WebSettings.LOAD_DEFAULT
        }
        
        webView.setBackgroundColor(Color.TRANSPARENT)
    }
    
    /**
     * Setup WebView clients using their existing implementations
     */
    private fun setupWebViewClients(webView: WebView) {
        // Create file upload handler for this WebView
        fileUploadHandler = FileUploadHandler(context)
        
        // Use their existing WebChromeClient with file upload support
        webView.webChromeClient = CustomWebChromeClient(
            context = context,
            fragment = getFragmentFromContext(),
            filePickerLauncher = null // Will be handled by FileUploadHandler
        )
        
        // Use their existing WebViewClient
        webView.webViewClient = CustomWebViewClient(context)
        
        // Add their existing JavaScript interface
        webView.addJavascriptInterface(
            WebAppInterface(
                context = context,
                onReloadWebview = { reload() },
                onClose = { /* Handle close */ }
            ),
            JAVASCRIPT_INTERFACE
        )
    }
    
    /**
     * Try to get Fragment from context if available
     */
    private fun getFragmentFromContext(): Fragment? {
        // This would require additional context information
        // For now, return null and let FileUploadHandler handle it
        return null
    }
}