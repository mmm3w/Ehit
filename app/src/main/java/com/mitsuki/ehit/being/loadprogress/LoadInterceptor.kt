package com.mitsuki.ehit.being.loadprogress

import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoadInterceptor(private val onProgress: PublishSubject<Progress>) : Interceptor {

    companion object {
        private const val HEADER_MARK = "MTKMark"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return newResponse(chain.proceed(newRequest(chain.request())))
    }

    private fun newRequest(request: Request): Request {
        val urlPair = request.url.toString().clearFeature()
        if (urlPair.second.isEmpty()) return request
        return request.newBuilder()
            .url(urlPair.first)
            .header(HEADER_MARK, urlPair.second)
            .build()
    }

    private fun newResponse(response: Response): Response {
        val key = response.request.header(HEADER_MARK)
        if (key.isNullOrEmpty()) return response
        val responseBody = response.body ?: throw IllegalStateException("null responseBody")
        return response.newBuilder()
            .body(ProgressResponseBody(key, responseBody, onProgress))
            .build()
    }
}