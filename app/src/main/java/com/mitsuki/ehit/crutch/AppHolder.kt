package com.mitsuki.ehit.crutch

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object AppHolder {
    private lateinit var mApplication: Application

    fun hold(application: Application) {
        mApplication = application
    }

    fun string(@StringRes id: Int): String = mApplication.getString(id)

    //请少用该方法弹出toast
    fun toast(
        text: String? = null,
        textRes: Int = -1,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        if (!text.isNullOrEmpty()) {
            Toast.makeText(mApplication, text, duration).show()
            return
        }

        if (textRes > 0) {
            Toast.makeText(mApplication, string(textRes), duration).show()
            return
        }
    }

    val clipboardManager: ClipboardManager
        get() = mApplication.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}