package com.mitsuki.ehit.model.repository

import androidx.paging.PagingData
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.reponse.RateBack
import com.mitsuki.ehit.model.entity.reponse.VoteBack
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun galleryList(pageIn: GalleryListPageIn): Flow<PagingData<Gallery>>

    fun detailImage(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn
    ): Flow<PagingData<ImageSource>>


    suspend fun login(account: String, password: String): RequestResult<String>

    suspend fun galleryListSource(
        pageIn: GalleryListPageIn,
        page: Int
    ): RequestResult<PageInfo<Gallery>>

    suspend fun galleryDetailInfo(gid: Long, token: String): RequestResult<GalleryDetail>

    suspend fun galleryImageSrouce(
        gid: Long,
        token: String,
        page: Int,
        ignoreCache:Boolean = false
    ): RequestResult<PageInfo<ImageSource>>

    suspend fun rating(
        gid: Long,
        token: String,
        apiUid: Long,
        apiKey: String,
        rating: Float
    ): RequestResult<RateBack>

    suspend fun galleryPreview(gid: Long, token: String, index: Int)
            : RequestResult<GalleryPreview>


    fun favoriteList(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): Flow<PagingData<Gallery>>

//    suspend fun getGalleryPagePToke(gid: Long, token: String, index: Int)
//            : RequestResult<String>

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

    suspend fun downloadPage(gid: Long, token: String, index: Int): RequestResult<String>

    suspend fun favoritesSource(
        pageIn: FavouritePageIn,
        page: Int
    ): Response<Pair<PageInfo<Gallery>, Array<Int>>>

    suspend fun galleryDetailSource(
        mGid: Long,
        mToken: String,
        page: Int
    ): Response<Pair<GalleryDetail, PageInfo<ImageSource>>>

}