package com.mitsuki.ehit.being

import android.content.Context
import android.widget.ImageView
import coil.Coil
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.being.imageloadprogress.ProgressProvider
import com.mitsuki.ehit.being.network.FakeHeader
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.lang.Exception

object CoilProvider {
    private lateinit var mImageLoader: ImageLoader

    fun init(context: Context) {
        mImageLoader = ImageLoader.Builder(context)
            .okHttpClient(HttpRookie.client)
            .build()

        Coil.setImageLoader(mImageLoader)
    }
}