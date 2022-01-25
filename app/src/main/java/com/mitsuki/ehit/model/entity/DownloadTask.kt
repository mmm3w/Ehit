package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import android.util.SparseArray
import com.mitsuki.ehit.model.entity.db.DownloadNode
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadTask(
    val gid: Long,
    val token: String,
    val start: Int,
    val end: Int,
    val thumb: String,
    val title: String,
) : Parcelable {
    val total: Int get() = end - start + 1
    val key: String get() = "g:$gid-$token"
}