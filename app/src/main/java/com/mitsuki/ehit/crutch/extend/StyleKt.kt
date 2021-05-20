package com.mitsuki.ehit.crutch.extend

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View


fun Activity.whiteStyle() {
    var tag = 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        tag = tag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        tag = tag or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.navigationBarColor = Color.WHITE

    }
    window.decorView.systemUiVisibility = tag
}