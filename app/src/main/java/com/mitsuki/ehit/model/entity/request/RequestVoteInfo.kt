package com.mitsuki.ehit.model.entity.request

import com.mitsuki.ehit.const.ParamValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RequestVoteInfo(
    @Json(name = "apiuid") val apiUid: Long,
    @Json(name = "apikey") val apiKey: String,
    @Json(name = "gid") val galleryID: Long,
    @Json(name = "token") val token: String,
    @Json(name = "comment_id") val cid: Long,
    @Json(name = "comment_vote") val vote: Int,
    @Json(name = "method") val method: String = ParamValue.VOTE_COMMENT
)