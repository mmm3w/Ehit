package com.mitsuki.ehit.base

import android.app.Application
import coil.Coil
import coil.ImageLoaderFactory
import com.mitsuki.ehit.crutch.AppHolder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class EhApplication : Application() {

    @Inject
    lateinit var imageLoaderFactory: ImageLoaderFactory

    override fun onCreate() {
        super.onCreate()
        AppHolder.hold(this)
        Coil.setImageLoader(imageLoaderFactory)
    }
}