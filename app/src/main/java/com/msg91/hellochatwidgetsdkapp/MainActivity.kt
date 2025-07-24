package com.msg91.hellochatwidgetsdkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.msg91.chatwidget.ChatWidget

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        val chatView = ChatWidget(
            context = this,
            helloConfig = mapOf(
                "widgetToken" to "ec5d6",
                "email" to "aman@example.com"
            ),
//            widgetColor = "#FFFFFF",
            isCloseButtonVisible = true
        )
        chatView.loadWidget()  // or loadHtml()

        setContentView(chatView)
    }
}
