package com.mitsuki.ehit.model.page

import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.RequestKey
import java.lang.RuntimeException

class GalleryListPageIn(var pageSource: GalleryPageSource) : GeneralPageIn() {

    val targetUrl: String get() = pageSource.targetUrl
    val showContent: String get() = pageSource.showContent
    val type: GalleryPageSource.Type get() = pageSource.type

    var maxPage: Int = 0

    fun attachPage(source: UrlParams, index: Int) {
        if (index == START) return
        source.urlParams(RequestKey.PAGE, index.toString())
    }

    fun attachSearchKey(source: UrlParams) {
        pageSource.applyKey(source)
    }
}