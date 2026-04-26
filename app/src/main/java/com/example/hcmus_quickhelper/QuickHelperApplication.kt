package com.example.hcmus_quickhelper

import android.app.Application
import com.example.hcmus_quickhelper.core.auth.SessionManager

class QuickHelperApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}