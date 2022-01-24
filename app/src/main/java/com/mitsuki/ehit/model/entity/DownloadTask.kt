package com.mitsuki.ehit.model.entity

import android.os.Parcelable
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
    fun toAtomList(): List<DownloadAtom> {
        return (start..end).map { DownloadAtom(gid, token, it) }
    }

    fun toDownloadNodeList(): List<DownloadNode> {
        return (start..end).map { DownloadNode(gid, token, it, false) }
    }
}