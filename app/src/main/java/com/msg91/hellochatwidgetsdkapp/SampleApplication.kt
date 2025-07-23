package com.msg91.hellochatwidgetsdkapp

import android.app.Application
import com.msg91.chatwidget.HelloSDK

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Hello SDK
        HelloSDK.initialize(this)
    }
}
