package com.mitsuki.ehit.base

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mitsuki.ehit.crutch.ShareData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var shareData: ShareData

    override fun onResume() {
        super.onResume()
        if (shareData.spDisableScreenshots) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}