package com.mitsuki.ehit.model.entity

data class DownloadPriority(
    val gid: Long,
    val token: String,
    val page: Int,
    val priority: Long
) {
    val tag: String get() = "gp:$gid-$token-$page"
}