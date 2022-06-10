package com.mitsuki.ehit.model.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.mitsuki.ehit.crutch.extensions.tryUnlock
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

class SaveBitmapActivityResultContract :
    ActivityResultContract<Pair<String, Bitmap>, Pair<Bitmap, Uri>?>() {

    private val mLock = Mutex()
    private var bitmapCache: Bitmap? = null

    override fun createIntent(context: Context, input: Pair<String, Bitmap>): Intent {
        runBlocking { mLock.lock() }
        bitmapCache = input.second
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, input.first)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Bitmap, Uri>? {
        try {
            if (resultCode == Activity.RESULT_OK) {
                return (bitmapCache ?: return null) to (intent?.data ?: return null)
            } else {
                return null
            }
        } finally {
            bitmapCache = null
            mLock.tryUnlock()
        }
    }
}