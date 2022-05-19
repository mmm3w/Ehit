package com.mitsuki.ehit.model.repository

import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.reponse.RateBack
import com.mitsuki.ehit.model.entity.reponse.VoteBack
import com.mitsuki.ehit.model.page.FavouritePageIn
import java.io.File

interface Repository {

    suspend fun login(account: String, password: String): RequestResult<String>

    suspend fun galleryListSource(
        pageIn: GalleryListPageIn,
        page: Int
    ): RequestResult<PageInfo<Gallery>>

    suspend fun galleryDetailInfo(gid: Long, token: String): RequestResult<GalleryDetail>

    suspend fun galleryImageSource(
        gid: Long,
        token: String,
        page: Int,
        ignoreCache: Boolean = false
    ): RequestResult<PageInfo<ImageSource>>

    suspend fun rating(
        gid: Long,
        token: String,
        apiUid: Long,
        apiKey: String,
        rating: Float
    ): RequestResult<RateBack>

    suspend fun galleryImagePToken(gid: Long, token: String, index: Int)
            : RequestResult<String>

    suspend fun galleryPreview(gid: Long, token: String, index: Int)
            : RequestResult<GalleryPreview>

    suspend fun galleryPreview(retryUrl: String, gid: Long, token: String, index: Int)
            : RequestResult<GalleryPreview>




    suspend fun favorites(gid: Long, token: String, cat: Int): RequestResult<String>


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


    suspend fun favoritesSource(
        pageIn: FavouritePageIn,
        page: Int
    ): Response<Pair<PageInfo<Gallery>, Array<Int>>>

    suspend fun galleryDetailSource(
        mGid: Long,
        mToken: String,
        page: Int
    ): Response<Pair<GalleryDetail, PageInfo<ImageSource>>>

    suspend fun downloadThumb(gid: Long, token: String): RequestResult<File>

    suspend fun downloadPage(gid: Long, token: String, index: Int): RequestResult<File>

    suspend fun downloadFile(url: String, folder: File, name: String): RequestResult<File>

    fun queryGalleryName(gid: Long, token: String): String
}