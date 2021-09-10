package com.mitsuki.ehit.crutch.coil

import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.intercept.Interceptor
import coil.memory.MemoryCache
import coil.request.ImageResult
import coil.request.SuccessResult
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder

@ExperimentalCoilApi
class LoadBreakInterceptor : Interceptor {

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        return SuccessResult(
            AppHolder.drawable(R.drawable.ic_nsfw)
                ?: throw IllegalAccessException("Break down"),
            request,
            ImageResult.Metadata(
                MemoryCache.Key("LoadBreak"),
                false,
                DataSource.DISK,
                false
            )
        )
    }
}