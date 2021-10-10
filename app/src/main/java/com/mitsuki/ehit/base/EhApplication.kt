package com.mitsuki.ehit.base

import android.app.Application
import com.google.firebase.FirebaseApp
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.coil.CoilProvider
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.FakeHeader
import com.mitsuki.armory.systemoverlay.OverlayManager
import com.mitsuki.ehit.crutch.Log.debug
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.dev.overlay.OverlayTool
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class EhApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        /** 此处的顺序必须固定 **********************************************************************/
        AppHolder.hold(this)
        ShareData.init(this)
        RoomData.init(this)
        OverlayTool.init(this)
        CookieManager.init()
        /******************************************************************************************/


        HttpRookie.configOkHttp = {
            cookieJar(CookieJarImpl())
            addInterceptor(FakeHeader())
            addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })

            connectTimeout(1, TimeUnit.MINUTES)
            readTimeout(1, TimeUnit.MINUTES)
            writeTimeout(1, TimeUnit.MINUTES)
        }


        CoilProvider.init(this)

    }
}