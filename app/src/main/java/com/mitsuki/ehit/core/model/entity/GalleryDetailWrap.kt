package com.mitsuki.ehit.core.model.entity

class GalleryDetailWrap {
    lateinit var headInfo: DetailHeader
    lateinit var partInfo: DetailPart
    lateinit var tags: Array<TagSet>
    lateinit var comment: Array<Comment>
    lateinit var commentState: CommentState

    companion object {
        const val MAX_COMMENT = 5
    }

    data class DetailHeader(
        val thumb: String,
        val title: String,
        val uploader: String,
        val category: String
    ) {
        val categoryColor: Int = com.mitsuki.ehit.core.model.ehparser.Category.getColor(category)
    }

    data class DetailPart(
        val rating: Float,
        val ratingCount: Int,
        val page: Int
    )

    enum class CommentState {
        NoComments, AllLoaded, MoreComments
    }
}

fun Gallery.obtainHeader(): GalleryDetailWrap.DetailHeader {

    return GalleryDetailWrap.DetailHeader(thumb, title, uploader, category)
}

fun GalleryDetail.obtainOperating(): GalleryDetailWrap.DetailPart {
    return GalleryDetailWrap.DetailPart(rating, ratingCount, pages)
}


fun GalleryDetail.obtainComments(): Array<Comment> {

    val count = GalleryDetailWrap.MAX_COMMENT.coerceAtMost(commentSet.comments.size)

    return Array(count) { index -> commentSet.comments[index] }
}

fun GalleryDetail.obtainCommentState(): GalleryDetailWrap.CommentState {
    return when (commentSet.comments.size) {
        0 -> GalleryDetailWrap.CommentState.NoComments
        in 1..GalleryDetailWrap.MAX_COMMENT -> GalleryDetailWrap.CommentState.AllLoaded
        else -> GalleryDetailWrap.CommentState.MoreComments
    }
}