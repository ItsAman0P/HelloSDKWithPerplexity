package com.msg91.chatwidget.internal.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.FrameLayout
import com.msg91.chatwidget.utils.Constants
import com.msg91.chatwidget.utils.ColorUtils
import com.msg91.chatwidget.utils.DrawableUtils
import com.msg91.chatwidget.utils.Extensions

object ViewFactory {

    fun createCloseButton(context: Context, isModal: Boolean = false): ImageButton {
        val size = if (isModal) Constants.MODAL_CLOSE_BUTTON_SIZE_DP else Constants.CLOSE_BUTTON_SIZE_DP
        val margin = if (isModal) Constants.MODAL_CLOSE_BUTTON_MARGIN_DP else Constants.CLOSE_BUTTON_MARGIN_DP

        return ImageButton(context).apply {
            // Create close icon using text
//            text = "×"
//            setTextColor(ColorUtils.parseColor(Constants.TEXT_PRIMARY_COLOR))
//            textSize = Constants.TEXT_SIZE_LARGE_SP
//            typeface = Typeface.DEFAULT_BOLD
//            gravity = Gravity.CENTER

            background = DrawableUtils.createCloseButtonBackground(context)

            layoutParams = FrameLayout.LayoutParams(
                Extensions.dpToPx(context, size),
                Extensions.dpToPx(context, size)
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                marginEnd = Extensions.dpToPx(context, margin)
                topMargin = if (isModal) {
                    Extensions.getStatusBarHeight(context) + Extensions.dpToPx(context, margin)
                } else {
                    Extensions.dpToPx(context, margin)
                }
            }

            elevation = Constants.ELEVATION_HIGH_DP
            contentDescription = Constants.CONTENT_DESCRIPTION_CLOSE
        }
    }

    fun createLoadingView(context: Context): View {
        val container = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(ColorUtils.parseColor(Constants.SURFACE_COLOR))
        }

        // Create loading content container
        val loadingContent = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        // Create progress bar
        val progressBar = ProgressBar(context).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = Extensions.dpToPx(context, Constants.SPACING_MEDIUM_DP)
            }
        }

        // Create loading text
        val loadingText = TextView(context).apply {
            text = Constants.LOADING_TEXT
            textSize = Constants.TEXT_SIZE_MEDIUM_SP
            setTextColor(ColorUtils.parseColor(Constants.TEXT_PRIMARY_COLOR))
            gravity = Gravity.CENTER
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Create error message (initially hidden)
        val errorMessage = TextView(context).apply {
            text = Constants.ERROR_NETWORK
            textSize = Constants.TEXT_SIZE_MEDIUM_SP
            setTextColor(ColorUtils.parseColor(Constants.ERROR_COLOR))
            gravity = Gravity.CENTER
            visibility = View.GONE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(
                    Extensions.dpToPx(context, Constants.SPACING_MEDIUM_DP),
                    0,
                    Extensions.dpToPx(context, Constants.SPACING_MEDIUM_DP),
                    0
                )
            }
            id = View.generateViewId() // For referencing later
        }

        // Create retry button (initially hidden)
        val retryButton = Button(context).apply {
            text = Constants.BUTTON_TRY_AGAIN
            textSize = Constants.TEXT_SIZE_MEDIUM_SP
            setTextColor(ColorUtils.parseColor(Constants.SURFACE_COLOR))
            background = DrawableUtils.createRoundedBackground(
                context,
                ColorUtils.parseColor(Constants.PRIMARY_COLOR)
            )
            visibility = View.GONE
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = Extensions.dpToPx(context, Constants.SPACING_MEDIUM_DP)
            }
            setPadding(
                Extensions.dpToPx(context, Constants.SPACING_LARGE_DP),
                Extensions.dpToPx(context, Constants.SPACING_SMALL_DP + 4),
                Extensions.dpToPx(context, Constants.SPACING_LARGE_DP),
                Extensions.dpToPx(context, Constants.SPACING_SMALL_DP + 4)
            )
            id = View.generateViewId() // For referencing later
        }

        // Add views to loading content
        loadingContent.addView(progressBar)
        loadingContent.addView(loadingText)
        loadingContent.addView(errorMessage)
        loadingContent.addView(retryButton)

        // Add loading content to container
        container.addView(loadingContent)

        return container
    }

    fun showError(loadingView: View) {
        // Hide progress bar and loading text
        (loadingView as? FrameLayout)?.let { container ->
            val loadingContent = container.getChildAt(0) as? android.widget.LinearLayout
            loadingContent?.let { content ->
                if (content.childCount >= 4) {
                    content.getChildAt(0).visibility = View.GONE // Progress bar
                    content.getChildAt(1).visibility = View.GONE // Loading text
                    content.getChildAt(2).visibility = View.VISIBLE // Error message
                    content.getChildAt(3).visibility = View.VISIBLE // Retry button
                }
            }
        }
    }

    fun hideLoading(loadingView: View) {
        loadingView.visibility = View.GONE
    }
}
