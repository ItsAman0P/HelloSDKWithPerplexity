package com.msg91.hellochatwidgetsdkapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.msg91.hellochatwidgetsdkapp.ChatWidget
import com.msg91.hellosdk.HelloConfig
import com.msg91.hellosdk.interfaces.ChatWidgetListener
import com.msg91.hellosdk.sample.ui.LayoutHelper
import com.msg91.hellosdk.sample.ui.ViewFactory
import com.msg91.hellosdk.service.ApiService

class ContactUsActivity : AppCompatActivity() {

    private lateinit var chatWidget: ChatWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        setupChatWidget()

        // Setup action bar
        supportActionBar?.title = "Contact Us"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupUI() {
        // Create main vertical layout
        val mainLayout = LayoutHelper.createLinearLayoutVertical(this)

        // Create header
        val headerLayout = ViewFactory.createHeaderLayout(
            this,
            "How can we help you?",
            "Get instant support through our chat widget"
        )

        // Create chat widget
        chatWidget = ChatWidget(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // This makes it take remaining space
            )
        }

        // Add views to main layout
        mainLayout.addView(headerLayout)
        mainLayout.addView(chatWidget)

        // Set content view
        setContentView(mainLayout)
    }

    private fun setupChatWidget() {
        val config = HelloConfig(
            widgetToken = "YOUR_WIDGET_TOKEN",
            uniqueId = "user_123",
            name = "John Doe",
            number = "+1234567890",
            mail = "john@example.com"
        )

        chatWidget.configure(
            config = config,
            widgetColor = "#FFFF00",
            useKeyboardAvoidingView = true,
            listener = object : ChatWidgetListener {
                override fun onWidgetLoaded() {
                    ApiService.log("Widget loaded successfully")
                }

                override fun onError(error: String) {
                    Toast.makeText(this@ContactUsActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                }

                override fun onWidgetClosed() {
                    ApiService.log("Widget closed")
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        chatWidget.destroy()
    }
}
