package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.const.ParamValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestRateInfo(
    @Json(name = "apiuid") val apiUid: String,
    @Json(name = "apikey") val apiKey: String,
    @Json(name = "gid") val galleryID: String,
    @Json(name = "token") val token: String,
    @Json(name = "rating") val rating: Int,
    @Json(name = "method") val method: String = ParamValue.RATE_GALLERY
)