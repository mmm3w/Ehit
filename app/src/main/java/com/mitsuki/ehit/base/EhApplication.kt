package com.mitsuki.ehit.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil.Coil
import coil.ImageLoaderFactory
import com.google.android.material.color.DynamicColors
import com.mitsuki.ehit.const.Setting
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.dao.GalleryDao
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltAndroidApp
open class EhApplication : Application() {
    @Inject
    lateinit var imageLoaderFactory: ImageLoaderFactory

    @Inject
    lateinit var shareData: ShareData


    override fun onCreate() {
        super.onCreate()
        AppHolder.hold(this)
        Coil.setImageLoader(imageLoaderFactory)
        DynamicColors.applyToActivitiesIfAvailable(this)

        when (shareData.spTheme) {
            Setting.THEME_NORMAL -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Setting.THEME_NIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}