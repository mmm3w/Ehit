package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.network.Site

sealed class GalleryDataMeta {
    open var key: GalleryDataKey? = null
    abstract val targetUrl: String
    abstract val hint: String

    class Normal(key: GalleryDataKey) : GalleryDataMeta() {

        constructor(keyword: String = "") : this(GalleryDataKey.createByQuery(keyword))

        override var key: GalleryDataKey? = key
        override val targetUrl: String
            get() = Site.galleryList
        override val hint: String
            get() = key?.searchHint?.ifEmpty { string(R.string.text_search) }
                ?: string(R.string.text_search)
    }

    /**
     * 不可搜索，修改配置项后转为normal
     */
    class Uploader(val name: String) : GalleryDataMeta() {
        @Suppress("SetterBackingFieldAssignment")
        override var key: GalleryDataKey? = null
            set(value) {}

        override val targetUrl: String
            get() = Site.galleryListByUploader(name)
        override val hint: String = "uploader:$name"
    }

    /**
     * 不可搜索，修改配置项后转为normal
     */
    class Tag(val tag: String) : GalleryDataMeta() {
        @Suppress("SetterBackingFieldAssignment")
        override var key: GalleryDataKey? = null
            set(value) {}

        override val targetUrl: String
            get() = Site.galleryListByTag(tag)
        override val hint: String = tag
    }

    class Subscription(key: GalleryDataKey) : GalleryDataMeta() {

        constructor(keyword: String = "") : this(GalleryDataKey.createByQuery(keyword))

        override var key: GalleryDataKey? = key

        override val targetUrl: String
            get() = Site.galleryListBySubscription

        override val hint: String
            get() = key?.searchHint?.ifEmpty { string(R.string.text_menu_subscription) }
                ?: string(R.string.text_search)
    }

    /**
     * 不可搜索，无分页
     */
    object Popular : GalleryDataMeta() {
        @Suppress("SetterBackingFieldAssignment")
        override var key: GalleryDataKey? = null
            set(value) {}

        override val targetUrl: String
            get() = Site.galleryListByPopular
        override val hint: String = string(R.string.text_menu_whats_hot)
    }

    companion object {
        fun create(meta: Type, key: String): GalleryDataMeta {
            return when (meta) {
                Type.NORMAL -> Normal(key)
                Type.UPLOADER -> Uploader(key)
                Type.TAG -> Tag(key)
                Type.SUBSCRIPTION -> Subscription(key)
                Type.WHATS_HOT -> Popular
            }
        }
    }

    enum class Type {
        NORMAL,
        UPLOADER,
        TAG,
        SUBSCRIPTION,
        WHATS_HOT
    }
}