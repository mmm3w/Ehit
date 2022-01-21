package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class DownloadNode(
    val gid: Long,
    val token: String,
    val priority: Long = System.currentTimeMillis()
) {
    private val waiting: MutableMap<String, DownloadAtom> by lazy { hashMapOf() }
    private val finish: MutableMap<String, DownloadAtom> by lazy { hashMapOf() }
    fun append(data: List<DownloadAtom>) {
        data.forEach { }


    }
}