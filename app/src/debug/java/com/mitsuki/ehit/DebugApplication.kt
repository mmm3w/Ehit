package com.mitsuki.ehit

import com.mitsuki.ehit.base.EhApplication

class DebugApplication : EhApplication() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(this)
    }
}