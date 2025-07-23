package com.msg91.chatwidget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.*
import android.widget.FrameLayout
import kotlinx.coroutines.*
import org.json.JSONObject
import com.msg91.chatwidget.config.HelloConfig
import com.msg91.chatwidget.interfaces.ChatWidgetListener
import com.msg91.chatwidget.service.ApiService
import com.msg91.chatwidget.service.CobrowseManager
import com.msg91.chatwidget.service.WebViewSourceGenerator
import com.msg91.chatwidget.config.Events
import com.msg91.chatwidget.internal.ui.ViewFactory
import com.msg91.chatwidget.internal.webview.CustomWebViewClient
import com.msg91.chatwidget.internal.webview.CustomWebChromeClient
import com.msg91.chatwidget.internal.webview.WebAppInterface
import com.msg91.chatwidget.utils.Constants

class ChatWidget(
    context: Context,
    private var widgetColor: String = "",
    private var useKeyboardAvoidingView: Boolean = false
) : FrameLayout(context) {

    private var webView: WebView? = null
    private var loadingView: android.view.View? = null
    private var config: HelloConfig? = null
    private var listener: ChatWidgetListener? = null
    private var webViewKey = 1

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        setupView()
    }

    private fun setupView() {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        // Create loading view
        loadingView = ViewFactory.createLoadingView(context)
        addView(loadingView)

        // Create WebView
        webView = WebView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                useWideViewPort = true
                loadWithOverviewMode = true
            }
            webViewClient = EmbeddedWebViewClient()
            webChromeClient = EmbeddedWebChromeClient()

            // Add JavaScript interface
            addJavascriptInterface(EmbeddedWebAppInterface(), "Android")
        }

        addView(webView)
    }

    fun configure(
        config: HelloConfig,
        widgetColor: String? = null,
        useKeyboardAvoidingView: Boolean = false,
        listener: ChatWidgetListener? = null
    ) {
        this.config = config
        widgetColor?.let { this.widgetColor = it }
        this.useKeyboardAvoidingView = useKeyboardAvoidingView
        this.listener = listener

        // Register for cobrowse
        registerForCobrowse(config)

        // Load the widget
        loadWidget()
    }

    private fun registerForCobrowse(config: HelloConfig) {
        if (config.mail != null || config.uniqueId != null) {
            coroutineScope.launch {
                try {
                    val body = mutableMapOf<String, Any>()
                    config.uniqueId?.let { body["unique_id"] = it }
                    config.mail?.let { body["mail"] = it }

                    ApiService.log("Registering for cobrowse")
                    val uuid = ApiService.generateUUID(config.widgetToken, body)
                    uuid?.let { CobrowseManager.registerForCobrowse(it) }
                } catch (e: Exception) {
                    ApiService.log("Error in registerForCobrowse", e.message)
                }
            }
        }
    }

    private fun loadWidget() {
        config?.let { config ->
            webViewKey++
            val htmlContent = WebViewSourceGenerator.generateHtmlContent(
                helloConfig = config,
                widgetColor = widgetColor,
                isCloseButtonVisible = false // Always false for embedded widget
            )
            webView?.loadDataWithBaseURL(
                "https://blacksea.msg91.com",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )
        }
    }

    fun reload() {
        loadWidget()
    }

    fun destroy() {
        coroutineScope.cancel()
        webView?.destroy()
        webView = null
    }

    private inner class EmbeddedWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString() ?: return false

            return if (url.startsWith("https://blacksea.msg91.com") || url.startsWith("about:blank")) {
                false
            } else {
                // Open external URLs in browser
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    ApiService.log("Error opening URL: $url", e.message)
                }
                true
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Hide loading view
            loadingView?.let { ViewFactory.hideLoading(it) }
            listener?.onWidgetLoaded()
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            // Show error in loading view
            loadingView?.let { ViewFactory.showError(it) }
            listener?.onError(error?.description?.toString() ?: Constants.ERROR_GENERIC)
        }
    }

    private inner class EmbeddedWebChromeClient : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.grant(request.resources)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            ApiService.log("Console:", consoleMessage?.message() ?: "")
            return true
        }
    }

    private inner class EmbeddedWebAppInterface {
        @JavascriptInterface
        fun postMessage(message: String) {
            post {
                handleWebViewMessage(message)
            }
        }
    }

    private fun handleWebViewMessage(message: String) {
        try {
            val jsonObject = JSONObject(message)
            val type = jsonObject.optString("type", "")
            val data = jsonObject.optJSONObject("data")

            ApiService.log("[onMessage]", message)

            when (type) {
                Events.RELOAD.value -> {
                    loadWidget()
                }
                Events.CLOSE.value -> {
                    // Embedded widget doesn't close, but notify listener
                    listener?.onWidgetClosed()
                }
                Events.UUID.value -> {
                    val uuid = data?.optString("uuid")
                    uuid?.let { CobrowseManager.registerForCobrowse(it) }
                }
                Events.DOWNLOAD_ATTACHMENT.value -> {
                    val downloadUrl = data?.optString("url") ?: jsonObject.optString("data", "")
                    if (downloadUrl.isNotEmpty()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            ApiService.log("Error opening download URL:", e.message)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            ApiService.log("Error handling message:", e.message)
        }
    }
}

