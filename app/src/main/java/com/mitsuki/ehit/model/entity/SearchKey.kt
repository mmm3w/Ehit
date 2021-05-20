package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.crutch.Key
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.model.ehparser.Category
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchKey(
    @Key(RequestKey.SEARCH_KEY_WORD)
    var key: String = "",
    @Key(RequestKey.SEARCH_KEY_CATEGORY)
    var category: Int = 0,
    @Key(RequestKey.SEARCH_KEY_ADVSEARCH)
    var isAdvancedEnable: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_GALLERY_NAME)
    var isSearchGalleryName: Boolean = true,
    @Key(RequestKey.SEARCH_KEY_TAGS)
    var isSearchGalleryTags: Boolean = true,
    @Key(RequestKey.SEARCH_KEY_DESCRIPTION)
    var isSearchGalleryDescription: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_TORRENT_FILE_NAMES)
    var isSearchTorrentFilenames: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_ONLY_TORRENTS)
    var isOnlyShowGalleriesWithTorrents: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_LOW_POWER_TAGS)
    var isSearchLowPowerTags: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_DOWNVOTED_TAGS)
    var isSearchDownvotedTags: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_SHOW_EXPUNGED)
    var isShowExpungedGalleries: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_MINIMUM_RATING, RequestKey.SEARCH_KEY_RATING)
    var minimumRating: Int? = null,
    @Key(
        RequestKey.SEARCH_KEY_BETWEEN_PAGES,
        RequestKey.SEARCH_KEY_BETWEEN_PAGES_START,
        RequestKey.SEARCH_KEY_BETWEEN_PAGES_END
    )
    var betweenPages: Pair<Int, Int>? = null,
    @Key(RequestKey.SEARCH_KEY_DISABLE_LANGUAGE)
    var isDisableLanguageFilter: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_DISABLE_UPLOADER)
    var isDisableUploaderFilter: Boolean = false,
    @Key(RequestKey.SEARCH_KEY_DISABLE_TAGS)
    var isDisableTagsFilter: Boolean = false
) : Parcelable {

    val showContent: String
        get() {
            if (key.isNotEmpty()) return key
            Category.DATA.forEach {
                if (it.code == category && it.code != Category.UNKNOWN_CODE)
                    return it.text
            }
            return ""
        }

    private val categoryForSearch: String
        get() = (category.inv() and Category.ALL_CATEGORY).toString()

    fun addParams(source: UrlParams) {
        source.urlParams(RequestKey.SEARCH_KEY_WORD to key)
        source.urlParams(RequestKey.SEARCH_KEY_CATEGORY to categoryForSearch)
        if (isAdvancedEnable) {
            source.urlParams(RequestKey.SEARCH_KEY_ADVSEARCH to "1")
            if (isSearchGalleryName)
                source.urlParams(RequestKey.SEARCH_KEY_GALLERY_NAME to "on")
            if (isSearchGalleryTags)
                source.urlParams(RequestKey.SEARCH_KEY_TAGS to "on")
            if (isSearchGalleryDescription)
                source.urlParams(RequestKey.SEARCH_KEY_DESCRIPTION to "on")
            if (isSearchTorrentFilenames)
                source.urlParams(RequestKey.SEARCH_KEY_TORRENT_FILE_NAMES to "on")
            if (isOnlyShowGalleriesWithTorrents)
                source.urlParams(RequestKey.SEARCH_KEY_ONLY_TORRENTS to "on")
            if (isSearchLowPowerTags)
                source.urlParams(RequestKey.SEARCH_KEY_LOW_POWER_TAGS to "on")
            if (isSearchDownvotedTags)
                source.urlParams(RequestKey.SEARCH_KEY_DOWNVOTED_TAGS to "on")
            if (isShowExpungedGalleries)
                source.urlParams(RequestKey.SEARCH_KEY_SHOW_EXPUNGED to "on")
            if (isDisableLanguageFilter)
                source.urlParams(RequestKey.SEARCH_KEY_DISABLE_LANGUAGE to "on")
            if (isDisableUploaderFilter)
                source.urlParams(RequestKey.SEARCH_KEY_DISABLE_UPLOADER to "on")
            if (isDisableTagsFilter)
                source.urlParams(RequestKey.SEARCH_KEY_DISABLE_TAGS to "on")

            minimumRating?.apply {
                source.urlParams(RequestKey.SEARCH_KEY_MINIMUM_RATING to "on")
                source.urlParams(RequestKey.SEARCH_KEY_RATING to toString())
            }

            betweenPages?.apply {
                source.urlParams(RequestKey.SEARCH_KEY_BETWEEN_PAGES to "on")
                source.urlParams(RequestKey.SEARCH_KEY_BETWEEN_PAGES_START to first.toString())
                source.urlParams(RequestKey.SEARCH_KEY_BETWEEN_PAGES_END to second.toString())
            }
        }
    }
}