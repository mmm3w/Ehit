package com.mitsuki.ehit.model.repository

import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.model.entity.reponse.VoteBack

interface CommentRepository {

    suspend fun galleryComment(gid: Long, token: String, allComment: Boolean)
            : RequestResult<List<Comment>>

    suspend fun sendGalleryComment(gid: Long, token: String, comment: String): RequestResult<Int>

    suspend fun voteGalleryComment(
        apiKey: String,
        apiUid: Long,
        gid: Long,
        token: String,
        cid: Long,
        vote: Int
    ): RequestResult<VoteBack>

}