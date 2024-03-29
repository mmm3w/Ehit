package com.mitsuki.ehit.crutch.network.client

import com.mitsuki.ehit.crutch.network.FakeHeader
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.ProxySelector
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyApiClientCreator @Inject constructor(
    private val cookieJar: CookieJar,
    private val proxyManager: ProxySelector
) : ClientCreator {
    override fun create(): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .proxySelector(proxyManager)
            .addInterceptor(FakeHeader())
            .followRedirects(false)
            .followSslRedirects(false)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })
            .build()
    }
}