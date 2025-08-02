package com.msg91.chatwidget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Single Responsibility: File upload handling for WebView
 * 
 * Manages file selection and upload functionality using modern
 * Activity Result API while maintaining their existing logic.
 */
internal class FileUploadHandler(private val context: Context) {
    
    private var fileCallback: ValueCallback<Array<Uri>>? = null
    private var activityLauncher: ActivityResultLauncher<Intent>? = null
    
    companion object {
        private const val TAG = "FileUploadHandler"
    }
    
    /**
     * Handle file upload request from WebView
     * Uses their existing file chooser logic
     */
    fun handleFileUpload(
        callback: ValueCallback<Array<Uri>>?,
        params: WebChromeClient.FileChooserParams?
    ): Boolean {
        if (callback == null) return false
        
        fileCallback = callback
        
        return try {
            setupActivityLauncher()
            val intent = createFileChooserIntent(params)
            activityLauncher?.launch(intent)
            true
        } catch (e: Exception) {
            // Fallback to null result
            callback.onReceiveValue(null)
            false
        }
    }
    
    /**
     * Release resources
     */
    fun release() {
        fileCallback = null
        activityLauncher = null
    }
    
    /**
     * Setup activity result launcher
     */
    private fun setupActivityLauncher() {
        if (activityLauncher != null) return
        
        val fragmentActivity = getFragmentActivity()
        if (fragmentActivity != null) {
            activityLauncher = fragmentActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                handleFileResult(result.data)
            }
        }
    }
    
    /**
     * Create file chooser intent using their existing logic
     */
    private fun createFileChooserIntent(params: WebChromeClient.FileChooserParams?): Intent {
        return params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }
    
    /**
     * Handle file selection result
     */
    private fun handleFileResult(data: Intent?) {
        val uris = when {
            data?.clipData != null -> {
                // Multiple files
                val clipData = data.clipData!!
                Array(clipData.itemCount) { i ->
                    clipData.getItemAt(i).uri
                }.filter { validateFile(it) }.toTypedArray()
            }
            data?.data != null -> {
                // Single file
                val uri = data.data!!
                if (validateFile(uri)) arrayOf(uri) else emptyArray()
            }
            else -> null
        }
        
        fileCallback?.onReceiveValue(uris)
        fileCallback = null
    }
    
    /**
     * Basic file validation (no size limits as requested)
     */
    private fun validateFile(uri: Uri): Boolean {
        return try {
            // Basic validation - just check if URI is accessible
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get FragmentActivity from context
     */
    private fun getFragmentActivity(): FragmentActivity? {
        var ctx = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is FragmentActivity) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        return null
    }
}