package com.mitsuki.ehit.model.page

import android.os.Parcelable
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.model.entity.SearchKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class GalleryPageSource(val type: Type) : Parcelable {
    companion object {
        val DEFAULT_NORMAL = Normal(SearchKey.DEFAULT)
        val DEFAULT_SUBSCRIPTION = Subscription(SearchKey.DEFAULT)
        val POPULAR = Popular()
    }

    abstract val targetUrl: String

    abstract val showContent: String

    abstract val cacheKey: String

    abstract fun searchKeyProvider(): SearchKey

    open val hasPaging: Boolean = true

    open fun applyKey(source: UrlParams) {}

    @Parcelize
    data class Normal(val searchKey: SearchKey) : GalleryPageSource(Type.NORMAL) {
        constructor(key: String) : this(SearchKey(key))

        @IgnoredOnParcel
        override val targetUrl: String
            get() = Url.galleryList

        @IgnoredOnParcel
        override val showContent: String
            get() = searchKey.showContent

        @IgnoredOnParcel
        override val cacheKey: String
            get() = searchKey.key

        override fun searchKeyProvider(): SearchKey {
            return searchKey
        }

        override fun applyKey(source: UrlParams) {
            searchKey.addParams(source)
        }
    }

    /**
     * 不可搜索，修改配置项后转为normal
     */
    @Parcelize
    data class Uploader(val name: String) : GalleryPageSource(Type.UPLOADER) {
        @IgnoredOnParcel
        override val targetUrl: String
            get() = Url.galleryListByUploader(name)

        @IgnoredOnParcel
        override val showContent: String
            get() = "uploader:$name"

        @IgnoredOnParcel
        override val cacheKey: String
            get() = name

        override fun searchKeyProvider(): SearchKey {
            return SearchKey(showContent)
        }
    }

    /**
     * 不可搜索，修改配置项后转为normal
     */
    @Parcelize
    data class Tag(val tag: String) : GalleryPageSource(Type.TAG) {
        @IgnoredOnParcel
        override val targetUrl: String
            get() = Url.galleryListByTag(tag)

        @IgnoredOnParcel
        override val showContent: String
            get() = tag

        @IgnoredOnParcel
        override val cacheKey: String
            get() = tag

        override fun searchKeyProvider(): SearchKey {
            return SearchKey(tag)
        }
    }

    @Parcelize
    data class Subscription(val searchKey: SearchKey) : GalleryPageSource(Type.SUBSCRIPTION) {

        constructor(key: String) : this(SearchKey(key))

        @IgnoredOnParcel
        override val targetUrl: String
            get() = Url.galleryListBySubscription

        @IgnoredOnParcel
        override val showContent: String
            get() = searchKey.showContent

        @IgnoredOnParcel
        override val cacheKey: String
            get() = searchKey.key

        override fun searchKeyProvider(): SearchKey {
            return searchKey
        }

        override fun applyKey(source: UrlParams) {
            searchKey.addParams(source)
        }
    }

    /**
     * 不可搜索，无分页
     */
    @Parcelize
    class Popular : GalleryPageSource(Type.WHATS_HOT) {
        @IgnoredOnParcel
        override val targetUrl: String
            get() = Url.galleryListByPopular

        @IgnoredOnParcel
        override val showContent: String
            get() = ""

        @IgnoredOnParcel
        override val cacheKey: String
            get() = ""

        override fun searchKeyProvider(): SearchKey {
            throw IllegalStateException("Popular can not do search")
        }

        @IgnoredOnParcel
        override val hasPaging: Boolean
            get() = false
    }

    @Parcelize
    enum class Type : Parcelable {
        NORMAL,
        UPLOADER,
        TAG,
        SUBSCRIPTION,
        WHATS_HOT
    }
}