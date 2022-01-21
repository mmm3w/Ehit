package com.mitsuki.ehit.model.entity

data class DownloadAtom(
    val gid: Long,
    val token: String,
    val page: Int
) {
}