package com.mitsuki.ehit.base

import android.app.Application
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.coil.CoilInit
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.FakeHeader
import com.mitsuki.ehit.dev.overlay.OverlayTool
import dagger.hilt.android.HiltAndroidApp
import okhttp3.CookieJar
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class EhApplication : Application() {

    @Inject
    lateinit var cookieJar: CookieJar

    override fun onCreate() {
        super.onCreate()
        AppHolder.hold(this)
        OverlayTool.init(this)
        CoilInit.init(this, cookieJar)
    }
}