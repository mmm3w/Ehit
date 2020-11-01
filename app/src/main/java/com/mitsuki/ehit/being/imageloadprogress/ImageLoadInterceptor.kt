package com.mitsuki.ehit.being.imageloadprogress

import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.Exception

class ImageLoadInterceptor(private val onProgress: MutableLiveData<Progress>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val urlPair = request.url.toString().clearFeature()
        return if (urlPair.second.isEmpty()) {
            chain.proceed(request)
        } else {
            chain.proceed(request.newBuilder().url(urlPair.first).build()).run {
                val responseBody = body ?: throw Exception("null responseBody")
                newBuilder()
                    .body(ProgressResponseBody(urlPair.second, responseBody, onProgress))
                    .build()
            }
        }
    }
}