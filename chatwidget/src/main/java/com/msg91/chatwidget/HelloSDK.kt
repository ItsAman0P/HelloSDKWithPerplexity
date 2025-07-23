package com.msg91.chatwidget

import android.app.Activity
import android.content.Context
import com.msg91.chatwidget.config.HelloConfig
import com.msg91.chatwidget.interfaces.ChatWidgetListener
import com.msg91.chatwidget.service.ApiService
import com.msg91.chatwidget.utils.Constants
import com.msg91.chatwidget.utils.Logger

/**
 * Main SDK entry point for MSG91 Hello Chat Widget
 *
 * Initialize the SDK before using any components:
 * ```
 * HelloSDK.initialize(context)
 * ```
 *
 * For embedded widget usage:
 * ```
 * val chatWidget = ChatWidget(context)
 * chatWidget.configure(config, widgetColor, useKeyboardAvoidingView, listener)
 * ```
 *
 * For modal widget usage:
 * ```
 * HelloSDK.showModal(activity, config, widgetColor, listener)
 * ```
 */
object HelloSDK {

    private var isInitialized = false
    private lateinit var applicationContext: Context
    private var debugLogsEnabled = false

    /**
     * Initialize the Hello SDK with application context
     *
     * @param context Application context (will be converted to application context automatically)
     * @param enableDebugLogs Enable debug logging (default: false)
     */
    @JvmStatic
    @JvmOverloads
    fun initialize(context: Context, enableDebugLogs: Boolean = false) {
        this.applicationContext = context.applicationContext
        this.debugLogsEnabled = enableDebugLogs
        this.isInitialized = true

        log("HelloSDK initialized successfully")
        log("SDK Version: ${Constants.SDK_NAME}")
        log("Debug logs enabled: $enableDebugLogs")
    }

    /**
     * Check if SDK is initialized
     *
     * @return true if SDK is initialized, false otherwise
     */
    @JvmStatic
    fun isInitialized(): Boolean = isInitialized

    /**
     * Get application context
     *
     * @return Application context
     * @throws IllegalStateException if SDK is not initialized
     */
    @JvmStatic
    fun getContext(): Context {
        if (!isInitialized) {
            throw IllegalStateException("HelloSDK must be initialized before use. Call HelloSDK.initialize(context) first.")
        }
        return applicationContext
    }

    /**
     * Check if debug logs are enabled
     *
     * @return true if debug logs are enabled
     */
    @JvmStatic
    fun isDebugEnabled(): Boolean = debugLogsEnabled && isInitialized

    /**
     * Enable or disable debug logs at runtime
     *
     * @param enabled true to enable debug logs, false to disable
     */
    @JvmStatic
    fun setDebugEnabled(enabled: Boolean) {
        if (isInitialized) {
            debugLogsEnabled = enabled
            log("Debug logs ${if (enabled) "enabled" else "disabled"}")
        }
    }

    // MARK: - Convenience Methods for Modal Widget

    /**
     * Show chat modal with configuration
     *
     * @param activity Current activity
     * @param config Hello configuration
     * @param widgetColor Optional widget color (hex color string)
     * @param listener Optional event listener
     */
    @JvmStatic
    @JvmOverloads
    fun showModal(
        activity: Activity,
        config: HelloConfig,
        widgetColor: String? = null,
        listener: ChatWidgetListener? = null
    ) {
        ensureInitialized()

        try {
            val modal = ChatWidgetModal.getInstance(activity)
            modal.configure(
                config = config,
                widgetColor = widgetColor,
                listener = listener
            )
            modal.show()

            log("Modal shown for activity: ${activity::class.java.simpleName}")
        } catch (e: Exception) {
            log("Error showing modal: ${e.message}")
            listener?.onError("Failed to show chat modal: ${e.message}")
        }
    }

    /**
     * Hide chat modal
     *
     * @param activity Current activity
     */
    @JvmStatic
    fun hideModal(activity: Activity) {
        ensureInitialized()

        try {
            val modal = ChatWidgetModal.getInstance(activity)
            modal.hide()

            log("Modal hidden for activity: ${activity::class.java.simpleName}")
        } catch (e: Exception) {
            log("Error hiding modal: ${e.message}")
        }
    }

    /**
     * Check if modal is currently showing
     *
     * @param activity Current activity
     * @return true if modal is showing, false otherwise
     */
    @JvmStatic
    fun isModalShowing(activity: Activity): Boolean {
        ensureInitialized()

        return try {
            ChatWidgetModal.getInstance(activity).isShowing()
        } catch (e: Exception) {
            log("Error checking modal status: ${e.message}")
            false
        }
    }

    /**
     * Destroy modal instance for specific activity
     * Call this in Activity.onDestroy() to prevent memory leaks
     *
     * @param activity Current activity
     */
    @JvmStatic
    fun destroyModal(activity: Activity) {
        try {
            ChatWidgetModal.destroyInstance()
            log("Modal destroyed for activity: ${activity::class.java.simpleName}")
        } catch (e: Exception) {
            log("Error destroying modal: ${e.message}")
        }
    }

// MARK: - Convenience Methods for Embedded Widget

/**
 * Create and configure embedded chat widget
 *
 * @param context Context
 * @param config Hello configuration
 * @param widgetColor Optional widget color (hex color string)
 * @param use

