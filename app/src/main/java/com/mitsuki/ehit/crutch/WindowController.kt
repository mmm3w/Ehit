package com.mitsuki.ehit.crutch

import android.app.Activity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class WindowController(private val activity: Activity) {

    private var controller: WindowInsetsControllerCompat? =
        ViewCompat.getWindowInsetsController(activity.findViewById(android.R.id.content))

    private var stable = false

//    init {
//        activity.window.decorView.setOnApplyWindowInsetsListener { v, insets ->
//            //异形屏的适配可能也要放在这里
//            insets
//        }
//    }

    fun window(
        statusBarFit: Boolean = false,
        statusBarHide: Boolean = false,
        statusBarLight: Boolean = false,
        statusBarColor: Int = -1,
        navigationBarFit: Boolean = false,
        navigationBarHide: Boolean = false,
        navigationBarLight: Boolean = false,
        navigationBarColor: Int = -1,
    ) {
        var showTag = 0
        var hideTag = 0
        var insetsType = 0

        if (statusBarFit) insetsType = insetsType or WindowInsetsCompat.Type.statusBars()

        if (navigationBarFit) insetsType = insetsType or WindowInsetsCompat.Type.navigationBars()

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

        controller?.show(showTag)
        controller?.hide(hideTag)
        if (hideTag != 0) controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.isAppearanceLightStatusBars = statusBarLight
        controller?.isAppearanceLightNavigationBars = navigationBarLight

        if (statusBarColor >= 0) {
            activity.window.statusBarColor = statusBarColor
        }

        activity.window.attributes = activity.window.attributes.apply {
            fitInsetsTypes = insetsType
        }

        if (navigationBarColor >= 0) {
            activity.window.navigationBarColor = navigationBarColor
        }
    }


}