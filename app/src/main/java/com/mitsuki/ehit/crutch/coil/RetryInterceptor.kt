package com.mitsuki.ehit.crutch.coil

import coil.annotation.ExperimentalCoilApi
import coil.intercept.Interceptor
import coil.request.ErrorResult
import coil.request.ImageResult

@ExperimentalCoilApi
class RetryInterceptor(private val maxRetry: Int) : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        var retryCount = 0
        val request = chain.request
        var response: ImageResult = chain.proceed(request)
        while (response is ErrorResult && retryCount < maxRetry) {
            retryCount++
            response = chain.proceed(request)
        }
        return response
    }
}