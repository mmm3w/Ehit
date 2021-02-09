package com.mitsuki.ehit.being.extend

import android.util.Log
import com.mitsuki.ehit.BuildConfig

@Suppress("SpellCheckingInspection")
fun debug(str: String) {
    if (BuildConfig.DEBUG) {
        Log.e("EhitDebug", str)
    }
}