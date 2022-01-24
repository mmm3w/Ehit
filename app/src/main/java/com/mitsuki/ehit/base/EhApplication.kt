package com.mitsuki.ehit.base

import android.app.Application
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.coil.CoilInit
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.FakeHeader
import com.mitsuki.ehit.dev.overlay.OverlayTool
import dagger.hilt.android.HiltAndroidApp
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class EhApplication : Application() {

    @Inject
    lateinit var cookieJar: CookieJarImpl

    override fun onCreate() {
        super.onCreate()
        /** 此处的顺序必须固定 **********************************************************************/
        AppHolder.hold(this)
        OverlayTool.init(this)
        /******************************************************************************************/


        HttpRookie.configOkHttp = {
            cookieJar(cookieJar)
            addInterceptor(FakeHeader())
            addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })

            connectTimeout(1, TimeUnit.MINUTES)
            readTimeout(1, TimeUnit.MINUTES)
            writeTimeout(1, TimeUnit.MINUTES)
        }


        CoilInit.init(this, cookieJar)

    }
}