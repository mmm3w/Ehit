package com.mitsuki.ehit.base

import android.app.Application
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.coil.CoilInit
import dagger.hilt.android.HiltAndroidApp
import okhttp3.CookieJar
import javax.inject.Inject

@HiltAndroidApp
open class EhApplication : Application() {

    @Inject
    lateinit var cookieJar: CookieJar

    override fun onCreate() {
        super.onCreate()
        AppHolder.hold(this)
        CoilInit.init(this, cookieJar)
    }
}