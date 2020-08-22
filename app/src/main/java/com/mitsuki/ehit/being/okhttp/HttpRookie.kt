package com.mitsuki.ehit.being.okhttp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

object HttpRookie {
    val client by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .dns(MyDns())
            .build()
    }
}

suspend fun Request.execute(): Response? = withContext(Dispatchers.IO) {
    Log.e("asdf", "url ${this@execute.url}")
    HttpRookie.client.newCall(this@execute).execute()
}