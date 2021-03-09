package com.mitsuki.ehit.being.extend

import android.util.Log
import com.mitsuki.ehit.BuildConfig

@Suppress("SpellCheckingInspection")
fun debug(str: String) {
    if (!BuildConfig.DEBUG) return
    val thisMethodStack = java.lang.Exception().stackTrace[1]
    Log.e("EhitDebug", "${thisMethodStack.fileName + ":" + thisMethodStack.lineNumber}\t-->\t$str")
}