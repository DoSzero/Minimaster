package com.minicoin.minimaster.app

import android.app.Application
import android.util.Log
import com.onesignal.OneSignal
import com.orhanobut.hawk.Hawk

class App : Application() {

    private val ONESIGNAL_APP_ID = "2edab4f9-9734-4b8d-819d-354f9f23ae97"

    override fun onCreate() {
        super.onCreate()

        oneSignal()
        hawk()
    }

    private fun hawk() {
        Hawk.init(this).build()
        val s = Hawk.get("first", "first")
        Log.d("testHAWK", "HAWK: $s")
    }

    private fun oneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}

