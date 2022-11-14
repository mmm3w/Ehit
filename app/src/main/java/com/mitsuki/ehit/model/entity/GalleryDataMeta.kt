package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.network.site.ApiContainer

sealed class GalleryDataMeta {
    open var key: GalleryDataKey? = null
    abstract val targetUrl: String
    abstract val hint: String

    class Normal(key: GalleryDataKey) : GalleryDataMeta() {

        constructor(
            keyword: String = "",
            byQuery: Boolean = true
        ) : this(if (byQuery) GalleryDataKey.createByQuery(keyword) else GalleryDataKey(keyword))

        override var key: GalleryDataKey? = key
        override val targetUrl: String
            get() = ApiContainer.galleryList()
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
            get() = ApiContainer.galleryListByUploader(name)
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
            get() = ApiContainer.galleryListByTag(tag)
        override val hint: String = tag
    }

    class Subscription(key: GalleryDataKey) : GalleryDataMeta() {

        constructor(
            keyword: String = "",
            byQuery: Boolean = true
        ) : this(if (byQuery) GalleryDataKey.createByQuery(keyword) else GalleryDataKey(keyword))

        override var key: GalleryDataKey? = key

        override val targetUrl: String
            get() = ApiContainer.galleryListBySubscription()

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
            get() = ApiContainer.galleryListByPopular()
        override val hint: String = string(R.string.text_menu_whats_hot)
    }

    companion object {
        fun create(meta: Type, key: String, byQuery: Boolean = true): GalleryDataMeta {
            return when (meta) {
                Type.NORMAL -> Normal(key, byQuery)
                Type.UPLOADER -> Uploader(key)
                Type.TAG -> Tag(key)
                Type.SUBSCRIPTION -> Subscription(key, byQuery)
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