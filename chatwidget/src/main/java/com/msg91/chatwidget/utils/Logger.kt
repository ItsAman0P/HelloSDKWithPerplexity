package com.msg91.chatwidget.utils

import android.util.Log
import com.msg91.chatwidget.HelloSDK

/**
 * Centralized logging utility for Hello SDK
 *
 * Provides consistent logging across the SDK with debug control
 * and proper tag management for filtering in logcat.
 */
object Logger {

    private const val DEFAULT_TAG = "HelloSDK"
    private const val MAX_LOG_LENGTH = 4000 // Android log message limit

    /**
     * Log verbose message
     *
     * @param tag Log tag (optional, defaults to HelloSDK)
     * @param message Log message
     * @param throwable Optional throwable to log
     */
    @JvmStatic
    @JvmOverloads
    fun v(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (shouldLog()) {
            logLongMessage(Log.VERBOSE, tag, message, throwable)
        }
    }

    /**
     * Log debug message
     *
     * @param tag Log tag (optional, defaults to HelloSDK)
     * @param message Log message
     * @param throwable Optional throwable to log
     */
    @JvmStatic
    @JvmOverloads
    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (shouldLog()) {
            logLongMessage(Log.DEBUG, tag, message, throwable)
        }
    }

    /**
     * Log info message
     *
     * @param tag Log tag (optional, defaults to HelloSDK)
     * @param message Log message
     * @param throwable Optional throwable to log
     */
    @JvmStatic
    @JvmOverloads
    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (shouldLog()) {
            logLongMessage(Log.INFO, tag, message, throwable)
        }
    }

    /**
     * Log warning message
     *
     * @param tag Log tag (optional, defaults to HelloSDK)
     * @param message Log message
     * @param throwable Optional throwable to log
     */
    @JvmStatic
    @JvmOverloads
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (shouldLog()) {
            logLongMessage(Log.WARN, tag, message, throwable)
        }
    }

    /**
     * Log error message
     *
     * @param tag Log tag (optional, defaults to HelloSDK)
     * @param message Log message
     * @param throwable Optional throwable to log
     */
    @JvmStatic
    @JvmOverloads
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (shouldLog()) {
            logLongMessage(Log.ERROR, tag, message, throwable)
        }
    }

    /**
     * Log What a Terrible Failure message
     * This should only be used for conditions that should never happen
     *
     * @param tag Log tag (optional, defaults to HelloSDK)
     * @param message Log message
     * @param throwable Optional throwable to log
     */
    @JvmStatic
    @JvmOverloads
    fun wtf(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        // WTF logs are always shown regardless of debug setting
        logLongMessage(Log.ASSERT, tag, message, throwable)
    }

    // MARK: - Convenience Methods

    /**
     * Log API request
     *
     * @param url Request URL
     * @param method HTTP method
     * @param body Request body (optional)
     */
    @JvmStatic
    @JvmOverloads
    fun logApiRequest(url: String, method: String, body: String? = null) {
        if (shouldLog()) {
            val message = buildString {
                append("API Request: $method $url")
                body?.let { append("\nBody: $it") }
            }
            d("HelloSDK-API", message)
        }
    }

    /**
     * Log API response
     *
     * @param url Request URL
     * @param responseCode HTTP response code
     * @param response Response body (optional)
     */
    @JvmStatic
    @JvmOverloads
    fun logApiResponse(url: String, responseCode: Int, response: String? = null) {
        if (shouldLog()) {
            val message = buildString {
                append("API Response: $responseCode for $url")
                response?.let {
                    append("\nResponse: ${it.take(500)}") // Limit response length
                    if (it.length > 500) append("... (truncated)")
                }
            }
            d("HelloSDK-API", message)
        }
    }

    /**
     * Log WebView events
     *
     * @param event Event name
     * @param details Event details (optional)
     */
    @JvmStatic
    @JvmOverloads
    fun logWebViewEvent(event: String, details: String? = null) {
        if (shouldLog()) {
            val message = buildString {
                append("WebView Event: $event")
                details?.let { append(" - $it") }
            }
            d("HelloSDK-WebView", message)
        }
    }

    /**
     * Log UI events
     *
     * @param event Event name
     * @param component Component name (optional)
     * @param details Event details (optional)
     */
    @JvmStatic
