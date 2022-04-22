package com.mitsuki.ehit.model.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.mitsuki.ehit.const.DirManager
import java.io.File

class ExportZipActivityResultContract : ActivityResultContract<Array<String>, Pair<File, Uri?>>() {

    private lateinit var file: File

    override fun createIntent(context: Context, input: Array<String>): Intent {
        val title = input[0]
        val gid = input[1]
        val token = input[2]
        file = DirManager.downloadCache(gid.toLong(), token)
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, "${title}.zip")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<File, Uri?> {
        return file to (if (resultCode == Activity.RESULT_OK) intent?.data else null)
    }
}