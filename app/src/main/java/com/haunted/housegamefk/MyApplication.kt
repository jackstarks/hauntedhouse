package com.haunted.housegamefk

import android.app.Application
import com.onesignal.OneSignal

class MyApplication : Application() {
    private val ONESIGNAL_APP_ID = "3eb3a567-503d-4ccc-a3b3-de34726d94a6"

    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }


}