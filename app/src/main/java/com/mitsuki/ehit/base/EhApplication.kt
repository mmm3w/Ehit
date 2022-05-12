package com.mitsuki.ehit.base

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.di.ApiClientCreator
import com.mitsuki.ehit.crutch.di.CoilClientCreator
import com.mitsuki.ehit.crutch.network.client.ClientCreator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class EhApplication : Application() {

    @CoilClientCreator
    @Inject
    lateinit var coilClientCreator: ClientCreator

    override fun onCreate() {
        super.onCreate()
        AppHolder.hold(this)

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient(coilClientCreator.create())
                .crossfade(true)
                .error(R.drawable.ic_baseline_broken_image_24)
                .build()
        )
    }
}