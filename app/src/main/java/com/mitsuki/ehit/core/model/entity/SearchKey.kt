package com.mitsuki.ehit.core.model.entity

import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.being.Key
import com.mitsuki.ehit.const.RequestKey
import kotlinx.android.parcel.Parcelize

data class SearchKey(
    @Key(RequestKey.SEARCH_KEY_WORD) var key: String = "",
    @Key(RequestKey.SEARCH_KEY_CATEGORY) var category: Int = 0
) {

    val showContent: String
        get() {
            return key
        }

    fun addParams(source: UrlParams) {
        source.urlParams(RequestKey.SEARCH_KEY_WORD to key)
        source.urlParams(RequestKey.SEARCH_KEY_CATEGORY to category.toString())
    }
}