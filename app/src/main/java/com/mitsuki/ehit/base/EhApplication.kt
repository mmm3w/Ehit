package com.mitsuki.ehit.base

import android.app.Application
import com.mitsuki.ehit.being.CoilProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EhApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //TODO：注意缓存的配置
        CoilProvider.init(this)
    }
}