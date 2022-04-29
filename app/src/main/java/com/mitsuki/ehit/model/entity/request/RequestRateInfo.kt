package com.mitsuki.ehit.model.entity.request

import com.mitsuki.ehit.const.ParamsValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestRateInfo(
    @Json(name = "apiuid") val apiUid: Long,
    @Json(name = "apikey") val apiKey: String,
    @Json(name = "gid") val galleryID: String,
    @Json(name = "token") val token: String,
    @Json(name = "rating") val rating: Int,
    @Json(name = "method") val method: String = ParamsValue.RATE_GALLERY
)