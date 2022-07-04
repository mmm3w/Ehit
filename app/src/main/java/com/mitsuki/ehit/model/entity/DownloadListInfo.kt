package com.mitsuki.ehit.model.entity

import kotlinx.parcelize.IgnoredOnParcel


data class DownloadListInfo(
    val gid: Long,
    val token: String,
    val thumb: String,
    val local_thumb: String,
    val title: String,
    val total: Int,
    val completed: Int,
    val failed: Int,
) {
    override fun equals(other: Any?): Boolean {
        return other is DownloadListInfo &&
                other.gid == gid &&
                other.token == token &&
                other.thumb == thumb &&
                other.local_thumb == local_thumb &&
                other.title == title &&
                other.total == total &&
                other.completed == completed &&
                other.failed == failed
    }

    override fun hashCode(): Int {
        var result = gid.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + thumb.hashCode()
        result = 31 * result + local_thumb.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + total
        result = 31 * result + completed
        result = 31 * result + failed
        return result
    }

    @IgnoredOnParcel
    val itemTransitionName
        get() = "gallery:$gid$token"

    fun toGallery(): Gallery {
        return Gallery(
            gid,
            token,
            "",
            "",
            title,
            "",
            local_thumb.ifEmpty { thumb },
            emptyArray(),
            0f
        )
    }


}