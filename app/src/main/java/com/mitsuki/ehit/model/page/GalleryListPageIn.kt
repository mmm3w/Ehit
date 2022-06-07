package com.mitsuki.ehit.model.page

import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.GalleryDataMeta

class GalleryListPageIn(
    var meta: GalleryDataMeta
) : GeneralPageIn() {

    val targetUrl: String get() = meta.targetUrl
    val hintContent: String get() = meta.hint

    var maxPage: Int = 0

    fun attachParams(source: UrlParams, index: Int) {
        if (index != START) {
            source.urlParams(RequestKey.PAGE, index.toString())
        }
        meta.key?.addParams(source)
    }

    fun updateKey(key: GalleryDataKey) {
        meta.key = key
    }
}