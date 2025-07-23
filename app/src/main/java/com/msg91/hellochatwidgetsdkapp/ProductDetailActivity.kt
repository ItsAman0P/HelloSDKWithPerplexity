package com.msg91.hellochatwidgetsdkapp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.msg91.hellosdk.ChatWidgetModal
import com.msg91.hellosdk.HelloConfig
import com.msg91.hellosdk.interfaces.ChatWidgetListener
import com.msg91.hellosdk.sample.ui.LayoutHelper
import com.msg91.hellosdk.sample.ui.ViewFactory
import com.msg91.hellosdk.service.ApiService

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var chatModal: ChatWidgetModal
    private lateinit var fabHelp: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        setupChatModal()

        // Setup action bar
        supportActionBar?.title = "Product Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupUI() {
        // Create main coordinator layout
        val coordinatorLayout = LayoutHelper.createCoordinatorLayout(this)

        // Create scrollable content
        val scrollView = ScrollView(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Create content container
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16)
            )
        }

        // Add product information
        contentLayout.addView(createProductTitle())
        contentLayout.addView(createProductDescription())
        contentLayout.addView(createProductFeatures())
        contentLayout.addView(createHelpButton())

        // Create help FAB
        fabHelp = FloatingActionButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_help)
            backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(this@ProductDetailActivity, android.R.color.holo_orange_dark)
            imageTintList = androidx.core.content.ContextCompat.getColorStateList(this@ProductDetailActivity, android.R.color.white)
            setOnClickListener { chatModal.show() }
        }

        // Assemble layout
        scrollView.addView(contentLayout)
        coordinatorLayout.addView(scrollView)
        LayoutHelper.addFabToCoordinator(coordinatorLayout, fabHelp, this)

        setContentView(coordinatorLayout)
    }

    private fun createProductTitle(): TextView {
        return TextView(this).apply {
            text = "Amazing Product"
            textSize = 28f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#212529"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(24)
            }
        }
    }

    private fun createProductDescription(): TextView {
        return TextView(this).apply {
            text = "This is an amazing product that will solve all your problems. " +
                    "It comes with incredible features and outstanding support. " +
                    "Our team is always ready to help you with any questions you might have."
            textSize = 16f
            setTextColor(Color.parseColor("#6c757d"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(24)
            }
        }
    }

    private fun createProductFeatures(): TextView {
        return TextView(this).apply {
            text = "Features:\n\n" +
                    "• Feature 1: Amazing capability\n" +
                    "• Feature 2: Outstanding performance\n" +
                    "• Feature 3: 24/7 Support\n" +
                    "• Feature 4: Easy integration\n" +
                    "• Feature 5: Secure and reliable"
            textSize = 16f
            setTextColor(Color.parseColor("#212529"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(32)
            }
        }
    }

    private fun createHelpButton(): android.widget.Button {
        return ViewFactory.createButton(this, "Need Help? Chat with Us").apply {
            setOnClickListener { chatModal.show() }
        }
    }

    private fun setupChatModal() {
        chatModal = ChatWidgetModal.getInstance(this)

        val config = HelloConfig(
            widgetToken = "YOUR_WIDGET_TOKEN",
            uniqueId = "user_123",
            name = "John Doe",
            number = "+1234567890",
            mail = "john@example.com"
        )

        chatModal.configure(
            config = config,
            widgetColor = "#FF6600",
            listener = object : ChatWidgetListener {
                override fun onModalShown() {
                    ApiService.log("Modal shown from product detail")
                }

                override fun onModalHidden() {
                    ApiService.log("Modal hidden from product detail")
                }

                override fun onWidgetLoaded() {
                    ApiService.log("Widget loaded in product detail modal")
                }

                override fun onError(error: String) {
                    Toast.makeText(this@ProductDetailActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun dpToPx(dp: Int): Int {
        return android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatWidgetModal.destroyInstance()
    }
}
