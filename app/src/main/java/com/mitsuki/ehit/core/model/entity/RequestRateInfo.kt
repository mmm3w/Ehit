package com.mitsuki.ehit.core.model.entity

import com.mitsuki.ehit.const.ParaValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestRateInfo(
    @Json(name = "apiuid") val apiUid: String,
    @Json(name = "apikey") val apiKey: String,
    @Json(name = "gid") val galleryID: String,
    @Json(name = "token") val token: String,
    @Json(name = "rating") val rating: Int,
    @Json(name = "method") val method: String = ParaValue.RATE_GALLERY
)