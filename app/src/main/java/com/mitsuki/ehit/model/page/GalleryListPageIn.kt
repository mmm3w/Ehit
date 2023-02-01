package com.mitsuki.ehit.model.page

import com.mitsuki.ehit.crutch.network.ehcore.ApiContainer
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.model.entity.ListPageKey

class GalleryListPageIn(
    var meta: GalleryDataMeta
) {

    val hintContent: String get() = meta.hint

    val targetUrl: String get() = meta.targetUrl
    var key: GalleryDataKey?
        set(value) {
            if (value != meta.key) {
                meta.key = value
            }
        }
        get() = meta.key


    var prepKey: ListPageKey? = null

    fun mergePageKey(nextKey: ListPageKey?, prevKey: ListPageKey?): ListPageKey? {
        val newKey = prepKey?.let {
            if (it.isNext) {
                nextKey?.copy(jump = it.jump, seek = it.seek)
            } else {
                prevKey?.copy(jump = it.jump, seek = it.seek)
            }
        }
        prepKey = null
        return newKey
    }


}