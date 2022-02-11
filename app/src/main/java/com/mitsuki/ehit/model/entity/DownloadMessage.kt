package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadMessage(
    val gid: Long,
    val token: String,
    val start: Int,
    val end: Int,
    val thumb: String,
    val title: String,
) : Parcelable {
    val total: Int get() = end - start + 1
    val key: String get() = key(gid, token)

    companion object {
        fun key(g: Long, t: String) = "g:$g-$t"
    }
}