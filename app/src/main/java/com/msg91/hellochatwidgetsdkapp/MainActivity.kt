package com.msg91.hellochatwidgetsdkapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.msg91.chatwidget.ChatWidget
import com.msg91.chatwidget.config.HelloConfig

class MainActivity : AppCompatActivity() {
    
    private var currentWidgetToken = "ec5d6"
    private var currentEmail = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            
            // Remove bottom padding when keyboard is visible to avoid extra space
            v.setPadding(
                systemBars.left, 
                0, // Top padding handled by header
                systemBars.right, 
                if (isImeVisible) 0 else systemBars.bottom
            )
            insets
        }
        
        // Handle system bar top insets for header
        val headerContainer = findViewById<LinearLayout>(R.id.header_container)
        val originalPaddingTop = headerContainer.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(headerContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Apply top system bar inset as padding to the header
            v.setPadding(
                v.paddingLeft,
                originalPaddingTop + systemBars.top, // Add status bar height to original padding
                v.paddingRight,
                v.paddingBottom
            )
            insets
        }
        
        // Setup back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup config button
        findViewById<ImageButton>(R.id.btn_config).setOnClickListener {
            showConfigDialog()
        }

        // Initialize ChatWidget SDK only if not already added
        if (savedInstanceState == null) {
            val helloConfig = createHelloConfig()
            
            // Initialize the ChatWidget SDK with the new API
            ChatWidget.initialize(helloConfig)
            
            // Create and attach the ChatWidgetFragment
            val chatWidgetFragment = ChatWidget.createFragment(
                widgetColor = "#8686ac",
                isCloseButtonVisible = false, // No close button in embedded mode
                useKeyboardAvoidingView = true
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, chatWidgetFragment)
                .commit()
        }
    }
    
    private fun showConfigDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_config, null)
        
        val widgetTokenInput = dialogView.findViewById<TextInputEditText>(R.id.et_widget_token)
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.et_email_config)
        
        // Pre-populate with current values
        widgetTokenInput.setText(currentWidgetToken)
        emailInput.setText(currentEmail)
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        // Handle button clicks
        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        
        dialogView.findViewById<Button>(R.id.btn_save).setOnClickListener {
            val newWidgetToken = widgetTokenInput.text.toString().trim()
            val newEmail = emailInput.text.toString().trim()
            
            if (newWidgetToken.isNotEmpty()) {
                currentWidgetToken = newWidgetToken
                currentEmail = newEmail
                updateChatWidgetConfig()
                dialog.dismiss()
            } else {
                widgetTokenInput.error = "Widget token is required"
            }
        }
        
        dialog.show()
    }
    
    private fun createHelloConfig(): HelloConfig {
        val builder = HelloConfig.builder(currentWidgetToken)
        
        // Only include email key if email is not empty
        if (currentEmail.isNotEmpty()) {
            builder.addProperty("mail", currentEmail)
        }
        
        return builder.build()
    }
    
    private fun updateChatWidgetConfig() {
        try {
            val updatedConfig = createHelloConfig()
            ChatWidget.update(updatedConfig)
        } catch (e: Exception) {
            // Handle any config update errors gracefully
            e.printStackTrace()
        }
    }
}
