package com.mitsuki.ehit.crutch

import android.app.Application
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.showToast
import java.io.File

object AppHolder {
    private lateinit var mApplication: Application
    private var locked = true

    fun hold(application: Application) {
        mApplication = application
    }

    fun localBroadcastManager(): LocalBroadcastManager {
        return LocalBroadcastManager.getInstance(mApplication)
    }

    val isLocked get() = locked

    val fileDir: File get() = mApplication.filesDir

    val cacheDir: File get() = mApplication.cacheDir

    val externalCacheDir: File? get() = mApplication.externalCacheDir

    fun cacheDir(path: String): File = File(cacheDir, path)

    val contentResolver: ContentResolver get() = mApplication.contentResolver

    fun string(@StringRes id: Int): String = mApplication.getString(id)

    fun text(@StringRes id: Int): CharSequence = mApplication.getText(id)

    fun drawable(@DrawableRes id: Int): Drawable? = AppCompatResources.getDrawable(mApplication, id)

    fun color(@ColorRes id: Int): Int = ContextCompat.getColor(mApplication, id)

    //请少用该方法弹出toast
    fun toast(
        text: String? = null,
        textRes: Int = -1,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        if (!text.isNullOrEmpty()) {
            mApplication.showToast(text, duration)
            return
        }

        if (textRes > 0) {
            mApplication.showToast(text(textRes), duration)
            return
        }
    }

    fun lock() {
        locked = true
    }

    fun unlock() {
        locked = false
    }

    val clipboardManager: ClipboardManager
        get() = mApplication.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun setAnalyticsCollectionEnabled(enable: Boolean) {
        FirebaseAnalytics.getInstance(mApplication).setAnalyticsCollectionEnabled(enable)
    }

}