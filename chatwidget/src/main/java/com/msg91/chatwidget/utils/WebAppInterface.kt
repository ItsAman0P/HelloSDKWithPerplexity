package com.msg91.chatwidget.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import org.json.JSONObject

class WebAppInterface(
    private val context: Context,
    private val onEventReceived: (type: String, data: JSONObject?) -> Unit
) {

    @JavascriptInterface
    fun postMessage(jsonString: String) {
        println("[MSG91 EVENT]: [postMessage]: $jsonString")
        try {
            val json = JSONObject(jsonString)
            val type = json.optString("type")
            val data = json.optJSONObject("data") ?: run {
                // handle case where `data` might be a string
                val dataStr = json.opt("data")
                JSONObject().apply { put("url", dataStr) }
            }

            Handler(Looper.getMainLooper()).post {
                onEventReceived(type, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
