package com.msg91.chatwidget.interfaces

interface ChatWidgetListener {
    fun onWidgetLoaded() {}
    fun onWidgetClosed() {}
    fun onMessageReceived(message: String) {}
    fun onError(error: String) {}

    // Modal-specific events
    fun onModalShown() {}
    fun onModalHidden() {}
}