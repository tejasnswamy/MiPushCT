package com.example.xioamiintegration

import android.app.Application
import com.clevertap.android.sdk.ActivityLifecycleCallback

class MiPush : Application() {

    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        super.onCreate()
    }
}