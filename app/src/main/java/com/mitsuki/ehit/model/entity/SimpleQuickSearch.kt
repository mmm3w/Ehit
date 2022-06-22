package com.mitsuki.ehit.model.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleQuickSearch(
    val type: GalleryDataMeta.Type,
    val name: String,
    val keyword: String
)