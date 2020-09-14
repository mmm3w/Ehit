package com.mitsuki.ehit.core.model.entity

class GalleryDetailWrap {
    lateinit var headInfo: DetailHeader
    lateinit var partInfo: DetailPart
    lateinit var tags: Array<TagSet>
    lateinit var comment: Array<Comment>

    companion object {
        const val MAX_COMMENT = 5
    }

    data class DetailHeader(
        val thumb: String,
        val thumbTransitionName: String,
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

    data class DetailComment(
        val comments: Array<Comment>,
        val isAll: Boolean
    ) {
        companion object {
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DetailComment

            if (!comments.contentEquals(other.comments)) return false
            if (isAll != other.isAll) return false

            return true
        }

        override fun hashCode(): Int {
            var result = comments.contentHashCode()
            result = 31 * result + isAll.hashCode()
            return result
        }
    }
}

fun Gallery.obtainHeader(): GalleryDetailWrap.DetailHeader {

    return GalleryDetailWrap.DetailHeader(thumb, thumbTransitionName, title, uploader, category)
}

fun GalleryDetail.obtainOperating(): GalleryDetailWrap.DetailPart {
    return GalleryDetailWrap.DetailPart(rating, ratingCount, pages)
}


fun GalleryDetail.obtainComments(): Array<Comment> {

    val count = GalleryDetailWrap.MAX_COMMENT.coerceAtMost(commentSet.comments.size)

    return Array(count + 1) { index ->
        if (index >= count) {
            Comment(
                -1, "", "", when (commentSet.comments.size) {
                    0 -> "暂无评论"
                    in 1..count -> "已显示全部评论"
                    else -> "更多评论"
                }
            )
        } else {
            commentSet.comments[index]
        }
    }
}