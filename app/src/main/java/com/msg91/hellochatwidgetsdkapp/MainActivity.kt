package com.msg91.hellochatwidgetsdkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.msg91.chatwidget.ChatWidget

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.msg91.hellochatwidgetsdkapp.navigation.AppNavigation
import com.msg91.hellochatwidgetsdkapp.ui.HelloChatWidgetSDKAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloChatWidgetSDKAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//
//        val chatView = ChatWidget(
//            context = this,
//            helloConfig = mapOf(
//                "widgetToken" to "ec5d6",
//                "email" to "aman@example.com"
//            ),
////            widgetColor = "#FFFFFF",
//            isCloseButtonVisible = true
//        )
//        chatView.loadWidget()  // or loadHtml()
//
//        setContentView(chatView)
//    }
//}
