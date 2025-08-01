package com.msg91.chatwidget.webview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.msg91.chatwidget.utils.LogUtil
import org.json.JSONObject

class WebAppInterface(
    private val context: Context,
    private val onReload: () -> Unit,
    private val onClose: () -> Unit
) {
    
    @JavascriptInterface
    fun postMessage(jsonMessage: String) {
        LogUtil.log("🔥 [JavascriptInterface] postMessage called: $jsonMessage")
        Toast.makeText(context, "Interface working! Message: $jsonMessage", Toast.LENGTH_SHORT).show()
        val data = JSONObject(jsonMessage)
        val type = data.optString("type")


        when (type) {
            "test" -> {
                val message = data.optString("message")
                LogUtil.log("🎯 [TEST] Interface working! Message: $message")
                Toast.makeText(context, "✅ Interface Test Success!", Toast.LENGTH_LONG).show()
            }
            "reload" -> {
                onReload()
            }
            "close" -> {
                onClose()
            }
            "uuid" -> {
                val uuid = data.optString("uuid")
                // Log or register UUID for co-browsing
                LogUtil.log("[postMessage]: [UUID]: $jsonMessage")
            }
            "downloadAttachment" -> {
                Toast.makeText(context, "Download se", Toast.LENGTH_SHORT).show()
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
            "fileUploadTest" -> {
                val files = data.optJSONArray("files")
                LogUtil.log("[FileUpload] Test files selected: ${files?.toString()}")
            }
            else -> {
                // Unknown event
                LogUtil.log("[postMessage]: [Unhandled Event]: $type")
            }
        }
    }
}