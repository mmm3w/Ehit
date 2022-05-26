package com.mitsuki.ehit.crutch.coil

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.mitsuki.ehit.crutch.di.CoilClientCreator
import com.mitsuki.ehit.crutch.network.client.ClientCreator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MyImageLoaderFactory @Inject constructor(
    @ApplicationContext private val mContext: Context,
    @CoilClientCreator private val coilClientCreator: ClientCreator
) : ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(mContext)
            .okHttpClient(coilClientCreator.create())
            .crossfade(true)
            .build()
    }
}