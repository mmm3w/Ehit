package com.mitsuki.ehit.being

import android.app.Application
import androidx.annotation.StringRes

object AppHolder {

    private lateinit var mApplication: Application

    fun hold(application: Application) {
        mApplication = application
    }

    fun string(@StringRes id: Int): String = mApplication.getString(id)
}