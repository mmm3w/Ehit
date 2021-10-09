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
import com.mitsuki.ehit.R
import java.io.File

object AppHolder {
    private lateinit var mApplication: Application


    fun hold(application: Application) {
        mApplication = application
    }

    val fileDir: File get() = mApplication.filesDir

    val cacheDir: File get() = mApplication.cacheDir

    val externalCacheDir: File? get() = mApplication.externalCacheDir

    fun cacheDir(path: String): File = File(cacheDir, path)

    val contentResolver: ContentResolver get() = mApplication.contentResolver

    fun string(@StringRes id: Int): String = mApplication.getString(id)

    fun text(@StringRes id:Int):CharSequence = mApplication.getText(id)

    fun drawable(@DrawableRes id: Int): Drawable? = AppCompatResources.getDrawable(mApplication, id)

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