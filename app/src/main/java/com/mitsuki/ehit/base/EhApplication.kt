package com.mitsuki.ehit.base

import android.app.Application
import coil.util.CoilUtils
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.being.AppHolder
import com.mitsuki.ehit.being.CoilProvider
import com.mitsuki.ehit.being.ShareData
import com.mitsuki.ehit.being.db.RoomData
import com.mitsuki.ehit.being.network.FakeHeader
import com.mitsuki.ehit.being.network.MyDns
import com.mitsuki.ehit.being.network.Url
import dagger.hilt.android.HiltAndroidApp
import okhttp3.logging.HttpLoggingInterceptor

@HiltAndroidApp
class EhApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //TODO：注意缓存的配置

        AppHolder.hold(this)
        ShareData.init(this)

        HttpRookie.configOkHttp = {
            cache(CoilUtils.createDefaultCache(this@EhApplication))
            dns(MyDns())
//            cookieJar(CookieJarImpl(ShareData))
            addInterceptor(FakeHeader())
            addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })
//            addInterceptor(ProgressProvider.imageLoadInterceptor)
        }

        RoomData.init(this)
        CoilProvider.init(this)
        Url.initDomain(this)

    }
}