package com.msg91.chatwidget.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable

object DrawableUtils {

    fun createCloseIcon(context: Context, color: Int): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setSize(
                Extensions.dpToPx(context, Constants.CLOSE_BUTTON_SIZE_DP),
                Extensions.dpToPx(context, Constants.CLOSE_BUTTON_SIZE_DP)
            )
        }
    }

    fun createCloseButtonBackground(context: Context): StateListDrawable {
        val pressedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ColorUtils.parseColor(Constants.CLOSE_BUTTON_BG_HOVER_COLOR))
        }

        val normalDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(ColorUtils.parseColor(Constants.CLOSE_BUTTON_BG_COLOR))
        }

        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            addState(intArrayOf(), normalDrawable)
        }
    }

    fun createRoundedBackground(context: Context, color: Int, radiusDp: Int = 6): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = Extensions.dpToPx(context, radiusDp).toFloat()
        }
    }
}
