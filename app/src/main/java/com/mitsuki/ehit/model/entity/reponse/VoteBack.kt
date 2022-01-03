package com.mitsuki.ehit.model.entity.reponse

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoteBack(
    @Json(name = "comment_id") val commentId: Long,
    @Json(name = "comment_score") val commentScore: Int,
    @Json(name = "comment_vote") val commentVote: Int
)