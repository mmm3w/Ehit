package com.mitsuki.ehit.crutch

import android.content.Context
import coil.Coil
import coil.ImageLoader
import com.mitsuki.armory.httprookie.HttpRookie

object CoilProvider {
    fun init(context: Context) {
        Coil.setImageLoader(ImageLoader.Builder(context)
            .okHttpClient(HttpRookie.client)
            .availableMemoryPercentage(0.4)
            .build())
    }
}