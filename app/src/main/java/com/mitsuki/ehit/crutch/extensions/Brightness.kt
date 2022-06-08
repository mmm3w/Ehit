package com.mitsuki.ehit.crutch.extensions

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.FloatRange

fun Activity.getBrightness(): Float {
    return window.attributes.screenBrightness
}

fun Activity.isAutoBrightness(): Boolean {
    return window.attributes.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
}

fun Activity.setAutoBrightness() {
    window.attributes.apply {
        screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = this
    }
}

fun Activity.setBrightness(@FloatRange(from = 0.0, to = 1.0) value: Float) {
    window.attributes.apply {
        screenBrightness = value
        window.attributes = this
    }
}

fun Context.getSystemBrightness(): Float {
    return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
}