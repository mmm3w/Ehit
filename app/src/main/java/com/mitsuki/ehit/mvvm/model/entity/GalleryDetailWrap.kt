package com.mitsuki.ehit.mvvm.model.entity

import androidx.recyclerview.widget.DiffUtil

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
    val lang: String,
    val page: Int,
    val size: String,
    val favCount: Int,
    val posted: String
) : GalleryDetailItem()

data class DetailOperating(
    val rating: Float,
    val summary: String
) : GalleryDetailItem()

data class DetailTag(val tagSet: TagSet) : GalleryDetailItem()

data class DetailComment(
    val user: String,
    val posted: String,
    val content: String
) : GalleryDetailItem()

data class DetailPreview(
    val page: Int,
    val url: String
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

        fun parse(detail: GalleryDetail): ArrayList<GalleryDetailItem> {
            val list = ArrayList<GalleryDetailItem>()

            list.add(detail.obtainHeader())
            list.add(detail.obtainPart())
            list.add(detail.obtainOperating())
            list.addAll(detail.obtainTags())
            list.addAll(detail.obtainComments())

            return list
        }


    }
}

fun GalleryDetail.obtainHeader(): DetailHeader {
    return DetailHeader(detailThumb, title, uploader, category)
}

fun GalleryDetail.obtainPart(): DetailPart {
    return DetailPart(language, pages, size, favoriteCount, posted)
}

fun GalleryDetail.obtainOperating(): DetailOperating {
    return DetailOperating(rating, "")
}

fun GalleryDetail.obtainTags(): ArrayList<DetailTag> {
    return ArrayList<DetailTag>().also {
        for (item in tagSet) it.add(DetailTag(item))
    }
}

fun GalleryDetail.obtainComments(): ArrayList<DetailComment> {
    return ArrayList<DetailComment>().also {
        for (item in commentSet.comments) it.add(DetailComment(item.user, item.time, item.text))
    }
}
