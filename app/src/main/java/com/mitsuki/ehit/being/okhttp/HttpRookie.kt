package com.mitsuki.ehit.being.okhttp

import android.util.Log
import coil.util.CoilUtils
import com.mitsuki.ehit.being.CoilProvider
import com.mitsuki.ehit.being.imageloadprogress.ProgressProvider
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
            //TODO：这里的dns解析存在问题，日后需要解决
            .dns(MyDns())
            .addInterceptor(FakeHeader())
            .build()
    }
}

suspend fun Request.execute(): Response? = withContext(Dispatchers.IO) {
    Log.e("asdf", "url ${this@execute.url}")
    HttpRookie.client.newCall(this@execute).execute()
}