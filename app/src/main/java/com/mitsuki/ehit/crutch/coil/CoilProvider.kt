package com.mitsuki.ehit.crutch.coil

import android.content.Context
import coil.Coil
import coil.ImageLoader
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