package com.mitsuki.ehit.model.entity

data class DownloadNotifyProgress(
    val title: String,
    val total: Int,
    val completed: Int
)