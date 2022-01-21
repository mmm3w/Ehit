package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadTask(
    val gid: Long,
    val token: String,
    val start: Int,
    val end: Int,
) : Parcelable {
    val total: Int get() = end - start + 1
    val key: String get() = "g:$gid-$token"
    fun toAtomList(): List<DownloadAtom> {
        return (start..end).map { DownloadAtom(gid, token, it) }
    }
}