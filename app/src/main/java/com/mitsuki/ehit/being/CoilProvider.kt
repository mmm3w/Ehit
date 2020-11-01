package com.mitsuki.ehit.being

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.mitsuki.ehit.being.imageloadprogress.ProgressProvider
import com.mitsuki.ehit.being.okhttp.FakeHeader
import com.mitsuki.ehit.being.okhttp.HttpRookie
import com.mitsuki.ehit.being.okhttp.MyDns
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.lang.Exception

object CoilProvider {
    private lateinit var mImageLoader: ImageLoader
    private lateinit var mDefaultCache: Cache

    //TODO：在主要client的dns解析问题解决后将考虑共用一个client
    private val mImageLoadClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
            .cache(cache())
            .addInterceptor(FakeHeader())
            .addInterceptor(ProgressProvider.imageLoadInterceptor)
            .build()
    }

    fun imageLoader() = run {
        if (!this::mImageLoader.isInitialized) throw Exception("CoilProvider must be init")
        mImageLoader
    }

    private fun cache() = run {
        if (!this::mDefaultCache.isInitialized) throw Exception("CoilProvider must be init")
        mDefaultCache
    }

    fun init(context: Context) {
        mDefaultCache = CoilUtils.createDefaultCache(context)
        mImageLoader = ImageLoader.Builder(context)
            .okHttpClient(mImageLoadClient)
            .build()
    }
}

fun ImageView.load(url: String, builder: ImageRequest.Builder.() -> Unit = {}) {
    load(uri = url, imageLoader = CoilProvider.imageLoader(), builder = builder)
}