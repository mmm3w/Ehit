package com.mitsuki.ehit.crutch

import android.app.Activity
import android.view.WindowInsets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class WindowController(private val activity: Activity) {

    private val controller: WindowInsetsControllerCompat? by lazy {
        ViewCompat.getWindowInsetsController(activity.findViewById(android.R.id.content))
    }

    private var stable = false

//    init {
//        activity.window.decorView.setOnApplyWindowInsetsListener { v, insets ->
//            //异形屏的适配可能也要放在这里
//            insets
//        }
//    }

    fun window(
        statusBarHide: Boolean = false,
        statusBarLight: Boolean = false,
        statusBarColor: Int? = null,
        navigationBarHide: Boolean = false,
        navigationBarLight: Boolean = false,
        navigationBarColor: Int? = null,
        barFit: Boolean = true,
    ) {
        var showTag = 0
        var hideTag = 0

        if (statusBarHide) {
            hideTag = hideTag or WindowInsetsCompat.Type.statusBars()
            //TODO 对异形屏适配
        } else {
            showTag = showTag or WindowInsetsCompat.Type.statusBars()
        }

        if (navigationBarHide) {
            hideTag = hideTag or WindowInsetsCompat.Type.navigationBars()
        } else {
            showTag = showTag or WindowInsetsCompat.Type.navigationBars()
        }

        if (showTag != 0) controller?.show(showTag)
        if (hideTag != 0) controller?.hide(hideTag)
        if (hideTag != 0) controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.isAppearanceLightStatusBars = statusBarLight
        controller?.isAppearanceLightNavigationBars = navigationBarLight

        statusBarColor?.apply { activity.window.statusBarColor = this }
        navigationBarColor?.apply { activity.window.navigationBarColor = this }

        activity.window.setDecorFitsSystemWindows(barFit)
    }

//    fun windowBeforeCreate(
//        statusBarFit: Boolean = false,
//        navigationBarFit: Boolean = false
//    ) {
//        var insetsType = 0
//        if (statusBarFit) insetsType = insetsType or WindowInsetsCompat.Type.statusBars()
//        if (navigationBarFit) insetsType = insetsType or WindowInsetsCompat.Type.navigationBars()
//        activity.window.attributes.fitInsetsTypes = insetsType
//    }

}