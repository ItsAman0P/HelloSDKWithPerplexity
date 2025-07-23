package com.msg91.chatwidget.utils

import android.graphics.Color

object ColorUtils {

    fun parseColor(color: String): Int {
        return try {
            Color.parseColor(if (color.startsWith("#")) color else "#$color")
        } catch (e: IllegalArgumentException) {
            Color.parseColor(Constants.PRIMARY_COLOR)
        }
    }

    fun isColorLight(color: String): Boolean {
        return try {
            val colorInt = parseColor(color)
            val red = Color.red(colorInt)
            val green = Color.green(colorInt)
            val blue = Color.blue(colorInt)
            val brightness = (red * 299 + green * 587 + blue * 114) / 1000
            brightness > 155
        } catch (e: Exception) {
            false
        }
    }

    fun addAlpha(color: Int, alpha: Float): Int {
        val alphaInt = (alpha * 255).toInt()
        return Color.argb(alphaInt, Color.red(color), Color.green(color), Color.blue(color))
    }
}
