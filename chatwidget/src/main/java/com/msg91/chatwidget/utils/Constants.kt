package com.msg91.chatwidget.utils

import android.graphics.Color

object Constants {
    // Colors
    const val PRIMARY_COLOR = "#007bff"
    const val PRIMARY_DARK_COLOR = "#0056b3"
    const val ACCENT_COLOR = "#28a745"
    const val BACKGROUND_COLOR = "#f8f9fa"
    const val SURFACE_COLOR = "#ffffff"
    const val ERROR_COLOR = "#dc3545"
    const val TEXT_PRIMARY_COLOR = "#212529"
    const val TEXT_SECONDARY_COLOR = "#6c757d"
    const val CLOSE_BUTTON_BG_COLOR = "#33000000"
    const val CLOSE_BUTTON_BG_HOVER_COLOR = "#4d000000"

    // Dimensions
    const val CLOSE_BUTTON_SIZE_DP = 32
    const val CLOSE_BUTTON_MARGIN_DP = 16
    const val MODAL_CLOSE_BUTTON_SIZE_DP = 40
    const val MODAL_CLOSE_BUTTON_MARGIN_DP = 20

    const val SPACING_SMALL_DP = 8
    const val SPACING_MEDIUM_DP = 16
    const val SPACING_LARGE_DP = 24

    const val TEXT_SIZE_SMALL_SP = 12f
    const val TEXT_SIZE_MEDIUM_SP = 16f
    const val TEXT_SIZE_LARGE_SP = 18f

    const val ELEVATION_LOW_DP = 2f
    const val ELEVATION_MEDIUM_DP = 4f
    const val ELEVATION_HIGH_DP = 8f

    // URLs
    const val PROD_WIDGET_URL = "https://blacksea.msg91.com/chat-widget.js"
    const val GENERATE_UUID_URL = "https://api.phone91.com/v2/pubnub-channels/list/"

    // Strings
    const val SDK_NAME = "Hello SDK"
    const val LOADING_TEXT = "Loading chat..."
    const val ERROR_NETWORK = "Failed to load chat. Please check your connection and try again."
    const val ERROR_GENERIC = "Something went wrong. Please try again."
    const val BUTTON_TRY_AGAIN = "Try Again"
    const val BUTTON_CLOSE = "Close"
    const val CONTENT_DESCRIPTION_CLOSE = "Close chat"

    // Cobrowse
    const val COBROWSE_LICENSE = "FZBGaF9-Od0GEQ"
}
