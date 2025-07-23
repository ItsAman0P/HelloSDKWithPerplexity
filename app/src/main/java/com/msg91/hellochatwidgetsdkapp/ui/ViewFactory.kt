package com.msg91.hellosdk.sample.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

object ViewFactory {

    fun createHeaderLayout(context: Context, title: String, subtitle: String): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#007bff"))
            setPadding(dpToPx(context, 16), dpToPx(context, 16), dpToPx(context, 16), dpToPx(context, 16))

            addView(createHeaderTitle(context, title))
            addView(createHeaderSubtitle(context, subtitle))
        }
    }

    private fun createHeaderTitle(context: Context, text: String): TextView {
        return TextView(context).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun createHeaderSubtitle(context: Context, text: String): TextView {
        return TextView(context).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 16f
            alpha = 0.9f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(context, 8)
            }
        }
    }

    fun createMainTitle(context: Context, text: String): TextView {
        return TextView(context).apply {
            this.text = text
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#212529"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, 32)
            }
        }
    }

    fun createButton(context: Context, text: String, marginBottom: Int = 16): Button {
        return Button(context).apply {
            this.text = text
            textSize = 16f
            setBackgroundColor(Color.parseColor("#007bff"))
            setTextColor(Color.WHITE)
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, marginBottom)
            }

            // Add some padding
            setPadding(
                dpToPx(context, 16),
                dpToPx(context, 12),
                dpToPx(context, 16),
                dpToPx(context, 12)
            )
        }
    }

    fun createFloatingActionButton(context: Context): FloatingActionButton {
        return FloatingActionButton(context).apply {
            setImageResource(android.R.drawable.ic_dialog_email) // Using system icon
            backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.holo_blue_bright)
            imageTintList = ContextCompat.getColorStateList(context, android.R.color.white)
        }
    }

    fun createMainContainer(context: Context): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(context, 16), dpToPx(context, 16), dpToPx(context, 16), dpToPx(context, 16))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
