package com.mitsuki.ehit.crutch

import android.app.Activity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment

class WindowController(view: View, private val windowProvider: () -> Window?) {

    private val controller: WindowInsetsControllerCompat? by lazy {
        ViewCompat.getWindowInsetsController(view)
    }

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
        if (hideTag != 0) controller?.apply {
            hide(hideTag)
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        controller?.isAppearanceLightStatusBars = statusBarLight
        controller?.isAppearanceLightNavigationBars = navigationBarLight

        statusBarColor?.apply { windowProvider()?.statusBarColor = this }
        navigationBarColor?.apply { windowProvider()?.navigationBarColor = this }

        windowProvider()?.setDecorFitsSystemWindows(barFit)
    }
}

fun Activity.windowController() = lazy {
    WindowController(findViewById(android.R.id.content)) { window }
}

fun DialogFragment.windowController() = lazy {
    WindowController(requireView()) { requireDialog().window }
}