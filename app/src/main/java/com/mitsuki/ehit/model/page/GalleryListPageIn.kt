package com.mitsuki.ehit.model.page

import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.entity.GalleryDataType

class GalleryListPageIn(
    var type: GalleryDataType,
    var key: GalleryDataKey
) : GeneralPageIn() {

    val targetUrl: String get() = type.targetUrl
    val hintContent: String get() = if (type.enableSearch && key.searchHint.isNotEmpty()) key.searchHint else type.hint

    var maxPage: Int = 0

    fun attachPage(source: UrlParams, index: Int) {
        if (index == START) return
        source.urlParams(RequestKey.PAGE, index.toString())
    }

    fun attachSearchKey(source: UrlParams) {
        if (type.enableSearch) {
            key.addParams(source)
        } else {
            throw IllegalStateException("Source $type can not do search")
        }
    }
}