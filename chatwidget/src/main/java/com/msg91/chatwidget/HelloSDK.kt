package com.msg91.chatwidget

//import android.app.Activity
//import android.content.Context
//import com.msg91.chatwidget.config.HelloConfig
//import com.msg91.chatwidget.interfaces.ChatWidgetListener
//
//object HelloSDK {
//    private var isInitialized = false
//    private lateinit var context: Context
//
//    fun initialize(context: Context) {
//        this.context = context.applicationContext
//        isInitialized = true
//    }
//
//    fun isInitialized(): Boolean = isInitialized
//
//    fun getContext(): Context {
//        if (!isInitialized) {
//            throw IllegalStateException("HelloSDK must be initialized before use")
//        }
//        return context
//    }
//
//    // Convenience method for modal
//    fun showModal(
//        activity: Activity,
//        config: HelloConfig,
//        widgetColor: String? = null,
//        listener: ChatWidgetListener? = null
//    ) {
//        val modal = ChatWidgetModal.getInstance(activity)
//        modal.configure(config, widgetColor, listener)
//        modal.show()
//    }
//
//    fun hideModal(activity: Activity) {
//        ChatWidgetModal.getInstance(activity).hide()
//    }
//
//    fun isModalShowing(activity: Activity): Boolean {
//        return ChatWidgetModal.getInstance(activity).isShowing()
//    }
//}
