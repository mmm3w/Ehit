package com.mitsuki.ehit.model.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.mitsuki.ehit.crutch.extensions.tryUnlock
import com.mitsuki.ehit.crutch.utils.fileNameTime
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

class ExportDataActivityResultContract :
    ActivityResultContract<IntArray, Pair<IntArray, Uri>?>() {

    private val mLock = Mutex()
    private var inputCache: IntArray? = null

    override fun createIntent(context: Context, input: IntArray): Intent {
        runBlocking { mLock.lock() }
        inputCache = input
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(
                Intent.EXTRA_TITLE,
                "ehit-${System.currentTimeMillis().fileNameTime()}.json"
            )
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<IntArray, Uri>? {
        try {
            if (resultCode == Activity.RESULT_OK) {
                return (inputCache ?: return null) to (intent?.data ?: return null)
            } else {
                return null
            }
        } finally {
            inputCache = null
            mLock.tryUnlock()
        }
    }
}