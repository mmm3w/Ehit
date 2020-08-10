package com.mitsuki.ehit.mvvm.model.entity

import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class GalleryDetailItem

data class DetailHeader(
    val thumbUrl: String,
    val title: String,
    val uploader: String,
    val category: String
) : GalleryDetailItem() {
    val categoryColor: Int = com.mitsuki.ehit.mvvm.model.ehparser.Category.getColor(category)
}

data class DetailPart(
    val rating: Float,
    val ratingCount: Int,
    val page: Int
) : GalleryDetailItem()

data class DetailOperating(
    val s: String = ""
) : GalleryDetailItem()

data class DetailTag(val tagSet: TagSet) : GalleryDetailItem()

data class DetailComment(
    val comments: ArrayList<Comment>,
    val isAll: Boolean
) : GalleryDetailItem()

data class DetailPreview(
    val page: Int,
    val source: ImageSource
) : GalleryDetailItem()

class GalleryDetailWrap {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GalleryDetailItem>() {
            override fun areItemsTheSame(
                oldConcert: GalleryDetailItem,
                newConcert: GalleryDetailItem
            ): Boolean =
                oldConcert == newConcert

            override fun areContentsTheSame(
                oldConcert: GalleryDetailItem,
                newConcert: GalleryDetailItem
            ): Boolean =
                oldConcert == newConcert
        }

        suspend fun parseCoroutines(detail: GalleryDetail?): ArrayList<GalleryDetailItem> {
            return withContext(Dispatchers.IO) { parse(detail) }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parse(detail: GalleryDetail?): ArrayList<GalleryDetailItem> {
            if (detail == null) return ArrayList()

            val list = ArrayList<GalleryDetailItem>()
            list.add(detail.obtainHeader())
            list.add(detail.obtainPart())
            list.add(detail.obtainOperating())
            list.addAll(detail.obtainTags())
            list.add(detail.obtainComments())
            list.addAll(detail.obtainPreview())
            return list
        }
    }
}

fun GalleryDetail.obtainHeader(): DetailHeader {
    return DetailHeader(detailThumb, title, uploader, category)
}

fun GalleryDetail.obtainPart(): DetailPart {
    return DetailPart(rating, ratingCount, pages)
}

fun GalleryDetail.obtainOperating(): DetailOperating {
    return DetailOperating()
}

fun GalleryDetail.obtainTags(): ArrayList<DetailTag> {
    return ArrayList<DetailTag>().also {
        for (item in tagSet) it.add(DetailTag(item))
    }
}

fun GalleryDetail.obtainComments(): DetailComment {
    return DetailComment(ArrayList<Comment>().also {
        for ((count, item) in commentSet.comments.withIndex()) {
            it.add(item)
            if (count + 1 > 1) break
        }
    }, commentSet.comments.size <= 2)
}

fun GalleryDetail.obtainPreview(): ArrayList<DetailPreview> {
    return ArrayList<DetailPreview>().also {
        for ((count, item) in images.withIndex()) it.add(DetailPreview(count + 1, item))
    }
}