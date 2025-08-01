package com.msg91.chatwidget.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.msg91.chatwidget.utils.LogUtil
import androidx.lifecycle.Lifecycle

class CustomWebChromeClient(
    private val context: Context,
    private val fragment: Fragment? = null,
    private val filePickerLauncher: ActivityResultLauncher<Intent>? = null
) : WebChromeClient() {
    
    companion object {
        private const val TAG = "CustomWebChromeClient"
        private const val FILE_CHOOSER_REQUEST_CODE = 1001
    }
    
    // FileUploadManager with proper initialization timing
    private var fileUploadManager: FileUploadManager? = null
    private var currentFileCallback: ValueCallback<Array<Uri>>? = null
    
    private fun getOrCreateFileUploadManager(): FileUploadManager {
        if (fileUploadManager == null) {
            LogUtil.log("[$TAG] Creating FileUploadManager")
            LogUtil.log("[$TAG] Fragment available: ${fragment != null}")
            LogUtil.log("[$TAG] Fragment state: ${fragment?.lifecycle?.currentState}")
            LogUtil.log("[$TAG] Fragment is added: ${fragment?.isAdded}")
            LogUtil.log("[$TAG] Context type: ${context.javaClass.simpleName}")
            LogUtil.log("[$TAG] Pre-registered launcher available: ${filePickerLauncher != null}")
            
            try {
                fileUploadManager = FileUploadManager(context, fragment, filePickerLauncher)
                LogUtil.log("[$TAG] FileUploadManager created successfully")
            } catch (e: Exception) {
                LogUtil.log("[$TAG] Error creating FileUploadManager: ${e.message}")
                e.printStackTrace()
                // Return a basic instance that will try fallback approaches
                fileUploadManager = FileUploadManager(context, null, null)
            }
        }
        return fileUploadManager!!
    }
    
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        LogUtil.log("[$TAG] ===== onShowFileChooser called =====")
        LogUtil.log("[$TAG] WebView: ${webView != null}")
        LogUtil.log("[$TAG] FilePathCallback: ${filePathCallback != null}")
        LogUtil.log("[$TAG] FileChooserParams: ${fileChooserParams != null}")
        
        // Log file chooser params details if available
        fileChooserParams?.let { params ->
            LogUtil.log("[$TAG] File chooser mode: ${params.mode}")
            LogUtil.log("[$TAG] Accepts MIME types: ${params.acceptTypes?.joinToString()}")
            LogUtil.log("[$TAG] Capture enabled: ${params.isCaptureEnabled}")
            LogUtil.log("[$TAG] Title: ${params.title}")
        }
        
        try {
            // Validate that we have essential components
            if (filePathCallback == null) {
                LogUtil.log("[$TAG] ERROR: FilePathCallback is null - cannot proceed")
                return false
            }
            
            // Store the callback for the result handler
            currentFileCallback = filePathCallback
            
            // Try to use pre-registered launcher first
            if (filePickerLauncher != null) {
                LogUtil.log("[$TAG] Using pre-registered ActivityResultLauncher")
                try {
                    val intent = fileChooserParams?.createIntent() ?: createDefaultFileIntent()
                    filePickerLauncher.launch(intent)
                    LogUtil.log("[$TAG] Pre-registered launcher started successfully")
                    return true
                } catch (e: Exception) {
                    LogUtil.log("[$TAG] Error with pre-registered launcher: ${e.message}")
                    // Fall through to FileUploadManager fallback
                }
            }
            
            // Fallback to FileUploadManager
            LogUtil.log("[$TAG] Using FileUploadManager fallback")
            val manager = getOrCreateFileUploadManager()
            val result = manager.handleFileChooser(filePathCallback, fileChooserParams)
            
            LogUtil.log("[$TAG] FileUploadManager result: $result")
            return result
            
        } catch (e: Exception) {
            LogUtil.log("[$TAG] ERROR in onShowFileChooser: ${e.message}")
            e.printStackTrace()
            
            // Always call the callback to prevent WebView from hanging
            try {
                filePathCallback?.onReceiveValue(null)
            } catch (callbackError: Exception) {
                LogUtil.log("[$TAG] Error clearing callback: ${callbackError.message}")
            }
            
            return false
        }
    }
    
    /**
     * Handle file picker result from pre-registered ActivityResultLauncher
     */
    fun handleFilePickerResult(resultCode: Int, data: Intent?) {
        LogUtil.log("[$TAG] handleFilePickerResult called with resultCode: $resultCode")
        
        try {
            val uris = when (resultCode) {
                Activity.RESULT_OK -> {
                    getSelectedFileUris(data)
                }
                Activity.RESULT_CANCELED -> {
                    LogUtil.log("[$TAG] File selection cancelled")
                    null
                }
                else -> {
                    LogUtil.log("[$TAG] Unknown result code: $resultCode")
                    null
                }
            }
            
            LogUtil.log("[$TAG] Selected files: ${uris?.size ?: 0}")
            currentFileCallback?.onReceiveValue(uris)
            
        } catch (e: Exception) {
            LogUtil.log("[$TAG] Error handling file picker result: ${e.message}")
            e.printStackTrace()
            currentFileCallback?.onReceiveValue(null)
        } finally {
            currentFileCallback = null
        }
    }
    
    /**
     * Extract selected file URIs from the result intent
     */
    private fun getSelectedFileUris(data: Intent?): Array<Uri>? {
        return when {
            data?.clipData != null -> {
                // Multiple files selected
                val clipData = data.clipData!!
                val uris = Array(clipData.itemCount) { i ->
                    clipData.getItemAt(i).uri
                }
                LogUtil.log("[$TAG] Multiple files selected: ${uris.size}")
                uris
            }
            data?.data != null -> {
                // Single file selected
                val uri = data.data!!
                LogUtil.log("[$TAG] Single file selected: $uri")
                arrayOf(uri)
            }
            else -> {
                LogUtil.log("[$TAG] No files selected")
                null
            }
        }
    }
    
    /**
     * Create a default file selection intent
     */
    private fun createDefaultFileIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }
    
    // Add other WebChromeClient methods for better debugging
    override fun onPermissionRequest(request: android.webkit.PermissionRequest?) {
        LogUtil.log("[$TAG] Permission request: ${request?.resources?.joinToString()}")
        try {
            request?.grant(request.resources)
            LogUtil.log("[$TAG] Permissions granted: ${request?.resources?.joinToString()}")
        } catch (e: Exception) {
            LogUtil.log("[$TAG] Error granting permissions: ${e.message}")
            request?.deny()
        }
    }
    
    override fun onConsoleMessage(message: android.webkit.ConsoleMessage?): Boolean {
        message?.let { msg ->
            val level = when (msg.messageLevel()) {
                android.webkit.ConsoleMessage.MessageLevel.ERROR -> "ERROR"
                android.webkit.ConsoleMessage.MessageLevel.WARNING -> "WARNING"
                android.webkit.ConsoleMessage.MessageLevel.DEBUG -> "DEBUG"
                else -> "INFO"
            }
            LogUtil.log("[$TAG] Console [$level]: ${msg.message()} (${msg.sourceId()}:${msg.lineNumber()})")
        }
        return true
    }
    
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (newProgress == 100) {
            LogUtil.log("[$TAG] Page loading completed")
        }
    }
    
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        LogUtil.log("[$TAG] Page title received: $title")
    }
    
    /**
     * Clean up resources when the WebChromeClient is no longer needed
     */
    fun cleanup() {
        try {
            LogUtil.log("[$TAG] Cleaning up CustomWebChromeClient")
            fileUploadManager?.cleanup()
            fileUploadManager = null
            LogUtil.log("[$TAG] CustomWebChromeClient cleanup completed")
        } catch (e: Exception) {
            LogUtil.log("[$TAG] Error during cleanup: ${e.message}")
            e.printStackTrace()
        }
    }
}