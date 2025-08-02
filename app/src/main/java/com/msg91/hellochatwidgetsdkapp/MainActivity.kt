package com.msg91.hellochatwidgetsdkapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.msg91.chatwidget.ChatWidgetFragment

class MainActivity : AppCompatActivity() {
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

        // Attach the ChatWidgetFragment only if not already added
        if (savedInstanceState == null) {
            val helloConfig = mapOf(
                "widgetToken" to "ec5d6",
                "email" to "aman@example.com"
            )

            val chatWidgetFragment = ChatWidgetFragment.newInstance(
                helloConfig = helloConfig,
                widgetColor = "#8686ac",
                isCloseButtonVisible = false, // No close button in embedded mode
                useKeyboardAvoidingView = true
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, chatWidgetFragment)
                .commit()
        }
    }
}
