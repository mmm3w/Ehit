package com.mitsuki.ehit.model.repository

import androidx.paging.PagingData
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

    fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>>


    fun favoriteList(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): Flow<PagingData<Gallery>>

    suspend fun galleryPreview(gid: Long, token: String, pToken: String, index: Int)
            : RequestResult<GalleryPreview>

    suspend fun galleryDetailWithPToken(gid: Long, token: String, index: Int)
            : RequestResult<String>

    suspend fun login(account: String, password: String): RequestResult<String>

    suspend fun rating(detail: GalleryDetail, rating: Float): RequestResult<RateBack>

    suspend fun favorites(gid: Long, token: String, cat: Int): RequestResult<String>

    suspend fun galleryComment(gid: Long, token: String, allComment: Boolean)
            : RequestResult<List<Comment>>

    suspend fun sendGalleryComment(gid: Long, token: String, comment: String): RequestResult<Int>

    suspend fun voteGalleryComment(
        apiKey:String,
        apiUid:Long,
        gid:Long,
        token:String,
        cid: Long,
        vote: Int
    ): RequestResult<VoteBack>

}