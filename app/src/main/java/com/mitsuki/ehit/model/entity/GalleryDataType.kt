package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.network.Site

sealed class GalleryDataType {
    companion object {
        val DEFAULT_NORMAL = Normal()
        val DEFAULT_SUBSCRIPTION = Subscription()
        val DEFAULT_POPULAR = Popular
    }

    abstract val targetUrl: String
    abstract val enableSearch: Boolean
    abstract val hint: String

    class Normal : GalleryDataType() {
        override val targetUrl: String
            get() = Site.galleryList
        override val enableSearch: Boolean = true
        override val hint: String = string(R.string.text_menu_main_page)
    }

    /**
     * 不可搜索，修改配置项后转为normal
     */
    class Uploader(val name: String) : GalleryDataType() {
        override val targetUrl: String
            get() = Site.galleryListByUploader(name)
        override val enableSearch: Boolean = false
        override val hint: String = "uploader:$name"
    }

    /**
     * 不可搜索，修改配置项后转为normal
     */
    class Tag(val tag: String) : GalleryDataType() {
        override val targetUrl: String
            get() = Site.galleryListByTag(tag)
        override val enableSearch: Boolean = false
        override val hint: String = tag
    }

    class Subscription : GalleryDataType() {
        override val targetUrl: String
            get() = Site.galleryListBySubscription
        override val enableSearch: Boolean = true
        override val hint: String = string(R.string.text_menu_subscription)

    }

    /**
     * 不可搜索，无分页
     */
    object Popular : GalleryDataType() {
        override val targetUrl: String
            get() = Site.galleryListByPopular
        override val enableSearch: Boolean = false
        override val hint: String = string(R.string.text_menu_whats_hot)
    }

    enum class Type {
        NORMAL,
        UPLOADER,
        TAG,
        SUBSCRIPTION,
        WHATS_HOT
    }
}