package com.msg91.chatwidget

//import com.onesignal.OneSignal
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.msg91.chatwidget.service.HtmlBuilder
import com.msg91.chatwidget.utils.LogUtil
import org.json.JSONObject
import kotlin.text.any
import kotlin.text.startsWith

class ChatWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    helloConfig: Map<String, Any>,
    private val widgetColor: String? = null,
    private val isCloseButtonVisible: Boolean = true,
    private val useKeyboardAvoidingView: Boolean = true
) : FrameLayout(context, attrs) {

    private val helloConfig: MutableMap<String, Any> = helloConfig.toMutableMap()
    private val JAVASCRIPT_INTERFACE: String = "ReactNativeWebView"

    // Wrapper to receive system bar insets
    private val container = FrameLayout(context)
    private val webView = WebView(context)



    init {

        validateConfig()
//        setupStatusBar()
//        setupSystemBarInsets()
        setupWebView()
        loadHtmlContent()
//        applyKeyboardAvoiding()
        setupKeyboardAnimation() // ✅ smooth animation

//        setupKeyboardInsets()
//        setupSmoothKeyboardAnimation()
    }

    private fun validateConfig() {
        if (!helloConfig.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing `widget_token` in helloConfig")
        }
    }

    private fun setupStatusBar() {
        (context as? Activity)?.window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
        }
    }

    private fun setupSystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            // Apply top + bottom padding (bottom only if keyboard is NOT visible)
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                if (isImeVisible) 0 else systemBars.bottom
            )

            view.setBackgroundColor(Color.parseColor("#F5F5F5"))
            insets
        }
    }

    private fun setupWebView() {
        webView.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            settings.javaScriptEnabled = true
            setBackgroundColor(Color.TRANSPARENT)
            settings.allowFileAccess = true
            settings.allowContentAccess = true
//            webViewClient = WebViewClient()

            settings.domStorageEnabled = true
            addJavascriptInterface(
                WebAppInterface(),
                JAVASCRIPT_INTERFACE
            )
//          setBackgroundColor(Color.parseColor("#F5F5F5"))
            // ✅ Handle URL loading behavior
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest): Boolean {
                    val url = request.url.toString()

                    return if (
                        url.startsWith("https://blacksea.msg91.com") ||
                        url.startsWith("https://ctest.msg91.com") ||
                        url.startsWith("about:blank")
                    ) {
                        false
                    } else {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, request.url)
                        context.startActivity(intent)
                        true
                    }
                }


            }
        }

        container.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(webView)
            setBackgroundColor(Color.TRANSPARENT)
        }

        addView(container)
    }

    // JavaScript interface to receive messages from the WebView
    private inner class WebAppInterface {
        @JavascriptInterface
        fun postMessage(jsonMessage: String) {
            LogUtil.log("[postMessage]: $jsonMessage")
            val data = JSONObject(jsonMessage)
            val type = data.optString("type")

            when (type) {
                "reload" -> {
                    loadHtmlContent()
                }
                "close" -> {
                    // You can optionally remove the view or call a callback if needed
//                    (this.parent as? ViewGroup)?.removeView(this)
                }
                "uuid" -> {
                    val uuid = data.optString("uuid")
                    // Log or register UUID for co-browsing
                    LogUtil.log("[postMessage]: [UUID]: $jsonMessage")
                }
                "downloadAttachment" -> {
                    val downloadUrl = data.optString("url")
                    if (downloadUrl.isNotBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                else -> {
                    // Unknown event
                    LogUtil.log("[postMessage]: [Unhandled Event]: $type")
                }
            }
        }
    }

    fun updateHelloConfig(newHelloConfig: Map<String, Any>) {
        if (!newHelloConfig.containsKey("widgetToken")) {
            throw IllegalArgumentException("Missing `widgetToken` in updated helloConfig")
        }

        helloConfig.clear()
        helloConfig.putAll(newHelloConfig)

        // if newHelloConfig has "mail" or "unique_id" then
//        CobrowseManager.getUuid()

        loadHtmlContent()
    }

    private fun loadHtmlContent() {
        val html = HtmlBuilder.buildWebViewHtml(
            helloConfig = helloConfig,
            widgetColor = widgetColor,
            isCloseButtonVisible = isCloseButtonVisible
        )
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
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
        loadHtmlContent();
    }

    fun loadHtmlDirectly(html: String) {
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }
}

