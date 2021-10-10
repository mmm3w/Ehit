package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.crutch.coil.CacheKey

class GalleryDetailWrap {
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

    enum class CommentState {
        NoComments, AllLoaded, MoreComments
    }

    val isSourceInitialized: Boolean get() = this::sourceDetail.isInitialized
}


data class HeaderInfo(
    val thumb: String,
    val title: String,
    val uploader: String,
    val category: String,
    val cacheKey: String
) {
    val categoryColor: Int = com.mitsuki.ehit.model.ehparser.Category.getColor(category)

    constructor(info: Gallery)
            : this(
        info.thumb,
        info.title,
        info.uploader,
        info.category,
        CacheKey.thumbKey(info.gid, info.token)
    )
}