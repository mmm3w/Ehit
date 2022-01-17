package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.crutch.coil.CacheKey

class GalleryDetailWrap {
    var isHeaderUpdate = false

    var headerInfo: HeaderInfo = HeaderInfo.DEFAULT
        set(value) {
            if (value != field) {
                isHeaderUpdate = true
                field = value
            }
        }
    lateinit var partInfo: DetailPart
    lateinit var tags: Array<TagGroup>
    lateinit var comment: Array<Comment>
    lateinit var commentState: CommentState
    lateinit var sourceDetail: GalleryDetail

    companion object {
        const val MAX_COMMENT = 3
    }

    data class DetailPart(
        var rating: Float,
        var ratingCount: Int,
        val page: Int
    )

    data class HeaderInfo(
        val thumb: String,
        val title: String,
        val uploader: String,
        val category: String,
        val cacheKey: String
    ) {
        companion object {
            val DEFAULT = HeaderInfo("", "", "", "", "")
        }

        val categoryColor: Int = com.mitsuki.ehit.model.ehparser.Category.getColor(category)

        constructor(info: Gallery)
                : this(
            info.thumb,
            info.title,
            info.uploader,
            info.category,
            CacheKey.thumbKey(info.gid, info.token)
        )

        override fun equals(other: Any?): Boolean {
            return other is HeaderInfo &&
                    thumb == other.thumb &&
                    title == other.title &&
                    uploader == other.uploader &&
                    category == other.category &&
                    cacheKey == other.cacheKey
        }

        override fun hashCode(): Int {
            var result = thumb.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + uploader.hashCode()
            result = 31 * result + category.hashCode()
            result = 31 * result + cacheKey.hashCode()
            result = 31 * result + categoryColor
            return result
        }
    }

    enum class CommentState {
        NoComments, AllLoaded, MoreComments
    }

    val isSourceInitialized: Boolean get() = this::sourceDetail.isInitialized
}

