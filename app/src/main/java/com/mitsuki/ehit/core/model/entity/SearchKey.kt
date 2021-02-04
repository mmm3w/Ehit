package com.mitsuki.ehit.core.model.entity

import android.os.Parcelable
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.being.Key
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.core.model.ehparser.Category
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchKey(
    @Key(RequestKey.SEARCH_KEY_WORD) var key: String = "",
    @Key(RequestKey.SEARCH_KEY_CATEGORY) var category: Int = 0
) : Parcelable {

    val showContent: String
        get() {
            return key
        }

    private val categoryForSearch: String
        get() = (category.inv() and Category.ALL_CATEGORY).toString()

    fun addParams(source: UrlParams) {
        source.urlParams(RequestKey.SEARCH_KEY_WORD to key)
        source.urlParams(RequestKey.SEARCH_KEY_CATEGORY to categoryForSearch)
    }
}