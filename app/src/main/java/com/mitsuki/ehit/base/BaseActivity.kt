package com.mitsuki.ehit.base

import android.content.res.Configuration
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.Setting
import com.mitsuki.ehit.crutch.extensions.color
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.utils.windowController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var shareData: ShareData

    @Inject
    lateinit var memoryData: MemoryData

    val controller by windowController()

    override fun onResume() {
        super.onResume()
        if (memoryData.disableScreenshots) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        setBarStates()
    }

    fun setBarStates() {
        when (memoryData.theme) {
            Setting.THEME_NORMAL -> onUiMode(false)
            Setting.THEME_NIGHT -> onUiMode(true)
            else -> {
                val isNightMode =
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> true
                        else -> false
                    }
                onUiMode(isNightMode)
            }
        }
    }

    open fun onUiMode(isNightMode: Boolean) {
        controller.window(
            navigationBarLight = !isNightMode,
            statusBarLight = !isNightMode,
            navigationBarColor = color(R.color.navigation_bar_color),
            statusBarColor = color(R.color.status_bar_color)
        )
    }
}