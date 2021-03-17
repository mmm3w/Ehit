package com.mitsuki.ehit.being

import android.content.Context
import coil.Coil
import coil.ImageLoader
import com.mitsuki.armory.httprookie.HttpRookie

object CoilProvider {
    fun init(context: Context) {
        Coil.setImageLoader(ImageLoader.Builder(context)
            .okHttpClient(HttpRookie.client)
            .build())
    }
}