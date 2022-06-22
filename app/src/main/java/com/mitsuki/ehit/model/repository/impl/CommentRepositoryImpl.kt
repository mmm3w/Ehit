package com.mitsuki.ehit.model.repository.impl

import com.mitsuki.armory.httprookie.get
import com.mitsuki.armory.httprookie.post
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.armory.httprookie.request.json
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.network.Site
import com.mitsuki.ehit.crutch.moshi.toJson
import com.mitsuki.ehit.model.convert.GalleryCommentsConvert
import com.mitsuki.ehit.model.convert.SendCommentConvert
import com.mitsuki.ehit.model.convert.VoteBackConvert
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.model.entity.reponse.VoteBack
import com.mitsuki.ehit.model.entity.request.RequestVoteInfo
import com.mitsuki.ehit.model.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(val client: OkHttpClient) : CommentRepository {
    override suspend fun galleryComment(
        gid: Long,
        token: String,
        allComment: Boolean
    ): RequestResult<List<Comment>> {
        return withContext(Dispatchers.IO) {
            val data = client.get<List<com.mitsuki.ehit.model.entity.Comment>>(
                Site.galleryDetail(
                    gid,
                    token
                )
            ) {
                convert = GalleryCommentsConvert()
                if (allComment) urlParams(RequestKey.HC, "1")
            }
                .execute()
            try {
                when (data) {
                    is Response.Success<List<com.mitsuki.ehit.model.entity.Comment>> -> RequestResult.Success(
                        data.requireBody()
                    )
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }

    override suspend fun sendGalleryComment(
        gid: Long,
        token: String,
        comment: String
    ): RequestResult<Int> {
        val data = client.post<Int>(Site.galleryDetail(gid, token)) {
            convert = SendCommentConvert()
            urlParams(RequestKey.HC, "1")
            params(RequestKey.COMMENT_TEXT, comment)

            header(RequestKey.HEADER_ORIGIN, Site.currentDomain)
            header(RequestKey.HEADER_REFERER, url())
        }
            .execute()

        return try {
            when (data) {
                is Response.Success<Int> -> RequestResult.Success(0)
                is Response.Fail<*> -> throw  data.throwable
            }
        } catch (inner: Throwable) {
            RequestResult.Fail(inner)
        }
    }

    override suspend fun voteGalleryComment(
        apiKey: String,
        apiUid: Long,
        gid: Long,
        token: String,
        cid: Long,
        vote: Int
    ): RequestResult<VoteBack> {
        return withContext(Dispatchers.IO) {
            val data = client.post<VoteBack>(Site.api) {
                convert = VoteBackConvert()
                json(
                    RequestVoteInfo(
                        apiUid = apiUid,
                        apiKey = apiKey,
                        galleryID = gid,
                        token = token,
                        cid = cid,
                        vote = vote
                    ).toJson()
                )
                header(RequestKey.HEADER_ORIGIN, Site.currentDomain)
                header(RequestKey.HEADER_REFERER, Site.galleryDetail(gid, token))
            }
                .execute()
            try {
                when (data) {
                    is Response.Success<VoteBack> -> RequestResult.Success(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }
}