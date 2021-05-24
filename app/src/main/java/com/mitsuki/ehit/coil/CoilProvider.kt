package com.mitsuki.ehit.coil

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.bitmap.BitmapPool
import com.mitsuki.armory.httprookie.HttpRookie

object CoilProvider {
    private const val RETRY_TIMES = 3

    fun init(context: Context) {
        Coil.setImageLoader(ImageLoader.Builder(context)
            .okHttpClient(HttpRookie.client)
            .crossfade(true)
            .componentRegistry { add(RetryInterceptor(RETRY_TIMES)) }
            .build())
    }
}