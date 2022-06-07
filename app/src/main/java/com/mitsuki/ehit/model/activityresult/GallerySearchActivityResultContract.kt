package com.mitsuki.ehit.model.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.ui.search.SearchActivity

class GallerySearchActivityResultContract :
    ActivityResultContract<GalleryDataKey, GalleryDataKey?>() {
    override fun createIntent(context: Context, input: GalleryDataKey): Intent {
        return Intent(context, SearchActivity::class.java).apply {
            putExtra(DataKey.GALLERY_SEARCH_KEY, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GalleryDataKey? {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.getParcelableExtra<GalleryDataKey>(DataKey.GALLERY_SEARCH_KEY)
        } else {
            null
        }
    }
}