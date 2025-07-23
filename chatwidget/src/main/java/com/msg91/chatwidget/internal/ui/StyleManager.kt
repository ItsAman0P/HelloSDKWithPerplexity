package com.msg91.chatwidget.internal.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import com.msg91.chatwidget.utils.ColorUtils
import com.msg91.chatwidget.utils.Constants

object StyleManager {

    fun updateStatusBarForModal(activity: Activity, isModal: Boolean, widgetColor: String = "") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            if (isModal && widgetColor.isNotEmpty()) {
                try {
                    window.statusBarColor = ColorUtils.parseColor(widgetColor)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val isLight = ColorUtils.isColorLight(widgetColor)
                        var flags = window.decorView.systemUiVisibility
                        flags = if (isLight) {
                            flags or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        } else {
                            flags and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                        }
                        window.decorView.systemUiVisibility = flags
                    }
                } catch (e: Exception) {
                    // Fallback to default color
                    window.statusBarColor = ColorUtils.parseColor(Constants.PRIMARY_DARK_COLOR)
                }
            } else {
                // Restore default
                window.statusBarColor = ColorUtils.parseColor(Constants.PRIMARY_DARK_COLOR)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.decorView.systemUiVisibility = 0
                }
            }
        }
    }

    fun createFullScreenDialogTheme(): Int {
        // Return system theme ID since we can't reference custom themes
        return android.R.style.Theme_Black_NoTitleBar_Fullscreen
    }
}
