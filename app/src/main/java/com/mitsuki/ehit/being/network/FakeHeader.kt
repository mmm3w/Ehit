package com.mitsuki.ehit.being.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class FakeHeader : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        return chain.proceed(original.newBuilder().fakeHeader().build())
    }

    private fun Request.Builder.fakeHeader(): Request.Builder {
        addHeader(
            "accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        )
        addHeader("accept-language", "en-US,en;q=0.5")
        addHeader("cache-control", "max-age=0")
        addHeader(
            "user-agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36"
        )
        return this
    }
}

fun Request.Builder.fakeHeader(): Request.Builder {
    addHeader(
        "accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
    )
    addHeader("accept-language", "en-US,en;q=0.5")
    addHeader("cache-control", "max-age=0")
    addHeader(
        "user-agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36"
    )
    return this
}

