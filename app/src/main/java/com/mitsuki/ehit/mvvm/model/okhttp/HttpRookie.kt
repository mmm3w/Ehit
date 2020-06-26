package com.mitsuki.ehit.mvvm.model.okhttp

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpRookie{
    val client by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .dns(MyDns())
            .build()
    }





}