package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.crutch.coil.CacheKey

data class DetailHeader(
    val thumb: String,
    val title: String,
    val uploader: String,
    val category: String,
    val cacheKey: String
) {
    companion object {
        val DEFAULT = DetailHeader("", "", "", "", "")
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
        return other is DetailHeader &&
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

data class DetailPart(
    var rating: Float,
    var ratingCount: Int,
    val page: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is DetailPart &&
                rating == other.rating &&
                ratingCount == other.ratingCount &&
                page == other.page
    }

    override fun hashCode(): Int {
        var result = rating.hashCode()
        result = 31 * result + ratingCount
        result = 31 * result + page
        return result
    }
}


enum class CommentState {
    NoComments, AllLoaded, MoreComments
}



