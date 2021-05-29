package com.mitsuki.ehit.base

import android.app.Application
import coil.util.CoilUtils
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.coil.CoilProvider
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.FakeHeader
import com.mitsuki.ehit.crutch.network.MyDns
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.loadprogress.ProgressProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class EhApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppHolder.hold(this)
        RoomData.init(this)
        ShareData.init(this)

        HttpRookie.configOkHttp = {
            cache(CoilProvider.coilCache(this@EhApplication))
            dns(MyDns())
            cookieJar(CookieJarImpl(ShareData))
            addInterceptor(FakeHeader())
            addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })
            addInterceptor(ProgressProvider.imageLoadInterceptor)

            connectTimeout(5, TimeUnit.MINUTES)
            readTimeout(5, TimeUnit.MINUTES)
            writeTimeout(5, TimeUnit.MINUTES)
        }


        CoilProvider.init(this)
        Url.initDomain(this)
    }
}