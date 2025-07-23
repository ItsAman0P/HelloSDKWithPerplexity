package com.msg91.hellochatwidgetsdkapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.msg91.hellosdk.ChatWidgetModal
import com.msg91.hellosdk.HelloConfig
import com.msg91.hellosdk.HelloSDK
import com.msg91.hellosdk.interfaces.ChatWidgetListener
import com.msg91.hellosdk.sample.ui.LayoutHelper
import com.msg91.hellosdk.sample.ui.ViewFactory
import com.msg91.hellosdk.service.ApiService

class MainActivity : AppCompatActivity() {

    private lateinit var fabChat: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SDK
        HelloSDK.initialize(this)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Create main coordinator layout
        val coordinatorLayout = LayoutHelper.createCoordinatorLayout(this)

        // Create main content container
        val mainContainer = ViewFactory.createMainContainer(this)

        // Add title
        val title = ViewFactory.createMainTitle(this, "Hello SDK Sample")
        mainContainer.addView(title)

        // Add buttons
        val btnContactUs = ViewFactory.createButton(this, "Contact Us (Embedded)")
        val btnProductDetail = ViewFactory.createButton(this, "Product Detail (Modal)")
        val btnShowModal = ViewFactory.createButton(this, "Show Chat Modal", 0)

        mainContainer.addView(btnContactUs)
        mainContainer.addView(btnProductDetail)
        mainContainer.addView(btnShowModal)

        // Create floating action button
        fabChat = ViewFactory.createFloatingActionButton(this)

        // Add to coordinator layout
        coordinatorLayout.addView(mainContainer)
        LayoutHelper.addFabToCoordinator(coordinatorLayout, fabChat, this)

        // Set content view
        setContentView(coordinatorLayout)

        // Store button references for click listeners
        btnContactUs.id = android.R.id.button1
        btnProductDetail.id = android.R.id.button2
        btnShowModal.id = android.R.id.button3
    }

    private fun setupClickListeners() {
        findViewById<android.widget.Button>(android.R.id.button1).setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }

        findViewById<android.widget.Button>(android.R.id.button2).setOnClickListener {
            startActivity(Intent(this, ProductDetailActivity::class.java))
        }

        findViewById<android.widget.Button>(android.R.id.button3).setOnClickListener {
            showChatModal()
        }

        fabChat.setOnClickListener {
            showChatModal()
        }
    }

    private fun showChatModal() {
        val config = HelloConfig(
            widgetToken = "YOUR_WIDGET_TOKEN",
            uniqueId = "user_123",
            name = "John Doe",
            number = "+1234567890",
            mail = "john@example.com"
        )

        val modal = ChatWidgetModal.getInstance(this)
        modal.configure(
            config = config,
            widgetColor = "#FFFF00",
            listener = object : ChatWidgetListener {
                override fun onModalShown() {
                    ApiService.log("Modal shown")
                }

                override fun onModalHidden() {
                    ApiService.log("Modal hidden")
                }

                override fun onWidgetLoaded() {
                    ApiService.log("Widget loaded in modal")
                }

                override fun onError(error: String) {
                    Toast.makeText(this@MainActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        )
        modal.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatWidgetModal.destroyInstance()
    }
}
