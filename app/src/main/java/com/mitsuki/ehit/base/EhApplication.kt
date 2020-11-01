package com.mitsuki.ehit.base

import android.app.Application
import com.mitsuki.ehit.being.CoilProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EhApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CoilProvider.init(this)
    }
}