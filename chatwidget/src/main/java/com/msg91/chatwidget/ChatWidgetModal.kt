package com.msg91.chatwidget

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import kotlinx.coroutines.*
import org.json.JSONObject
import com.msg91.chatwidget.config.HelloConfig
import com.msg91.chatwidget.config.Events
import com.msg91.chatwidget.interfaces.ChatWidgetListener
import com.msg91.chatwidget.service.ApiService
import com.msg91.chatwidget.service.CobrowseManager
import com.msg91.chatwidget.service.WebViewSourceGenerator
import com.msg91.chatwidget.internal.ui.ViewFactory
import com.msg91.chatwidget.internal.ui.StyleManager
import com.msg91.chatwidget.utils.Constants
import com.msg91.chatwidget.utils.ColorUtils

class ChatWidgetModal private constructor(private val activity: Activity) {

    private var dialog: Dialog? = null
    private var webView: WebView? = null
    private var loadingView: android.view.View? = null
    private var config: HelloConfig? = null
    private var widgetColor: String = ""
    private var listener: ChatWidgetListener? = null
    private var webViewKey = 1

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        private var instance: ChatWidgetModal? = null

        fun getInstance(activity: Activity): ChatWidgetModal {
            if (instance == null || instance?.activity != activity) {
                instance?.destroy()
                instance = ChatWidgetModal(activity)
            }
            return instance!!
        }

        fun destroyInstance() {
            instance?.destroy()
            instance = null
        }
    }

    fun configure(
        config: HelloConfig,
        widgetColor: String? = null,
        listener: ChatWidgetListener? = null
    ) {
        this.config = config
        widgetColor?.let { this.widgetColor = it }
        this.listener = listener

        // Register for cobrowse
        registerForCobrowse(config)
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

    fun show() {
        if (dialog == null) {
            setupDialog()
        }

        config?.let {
            loadWidget()
        }

        dialog?.show()
        StyleManager.updateStatusBarForModal(activity, true, widgetColor)
        listener?.onModalShown()
    }

    fun hide() {
        dialog?.dismiss()
        StyleManager.updateStatusBarForModal(activity, false)
        listener?.onModalHidden()
    }

    fun isShowing(): Boolean = dialog?.isShowing == true

    private fun setupDialog() {
        dialog = Dialog(activity, StyleManager.createFullScreenDialogTheme()).apply {
            setContentView(createDialogLayout())
            setCancelable(true)
            setCanceledOnTouchOutside(false)

            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    hide()
                    true
                } else {
                    false
                }
            }

            setOnDismissListener {
                StyleManager.updateStatusBarForModal(activity, false)
                listener?.onModalHidden()
            }
        }
    }

    private fun createDialogLayout(): android.view.View {
        val rootLayout = FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(ColorUtils.parseColor(Constants.SURFACE_COLOR))
        }

        // Create loading view
        loadingView = ViewFactory.createLoadingView(activity)
        rootLayout.addView(loadingView)

        // Create WebView
        webView = WebView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
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
            webViewClient = ModalWebViewClient()
            webChromeClient = ModalWebChromeClient()

            // Add JavaScript interface
            addJavascriptInterface(ModalWebAppInterface(), "Android")
        }

        rootLayout.addView(webView)

        // Add close button
        val closeButton = ViewFactory.createCloseButton(activity, isModal = true)
        closeButton.setOnClickListener { hide() }
        rootLayout.addView(closeButton)

        // Setup retry button click in loading view
        setupRetryButton()

        return rootLayout
    }

    private fun setupRetryButton() {
        // This would need to be called after loading view is created
        // We'll handle this in the ViewFactory.createLoadingView method
    }

    private fun loadWidget() {
        config?.let { config ->
            webViewKey++
            val htmlContent = WebViewSourceGenerator.generateHtmlContent(
                helloConfig = config,
                widgetColor = widgetColor,
                isCloseButtonVisible = true // Always true for modal
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

    fun destroy() {
        coroutineScope.cancel()
        webView?.destroy()
        dialog?.dismiss()
        dialog = null
        webView = null
    }

    private inner class ModalWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString() ?: return false

            return if (url.startsWith("https://blacksea.msg91.com") || url.startsWith("about:blank")) {
                false
            } else {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    activity.startActivity(intent)
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

    private inner class ModalWebChromeClient : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.grant(request.resources)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            ApiService.log("Console:", consoleMessage?.message() ?: "")
            return true
        }
    }

    private inner class ModalWebAppInterface {
        @JavascriptInterface
        fun postMessage(message: String) {
            activity.runOnUiThread {
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
                    hide()
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
                            activity.startActivity(intent)
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
