package com.mitsuki.ehit.core.model.repository

import androidx.paging.PagingData
import com.mitsuki.ehit.being.network.RequestResult
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.*
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun galleryList(pageIn: PageIn): Flow<PagingData<Gallery>>

    fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: PageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>>

    suspend fun galleryPreview(gid: Long, token: String, index: Int): RequestResult<GalleryPreview>

    suspend fun galleryDetailWithPToken(gid: Long, token: String, index: Int): RequestResult<String>

    suspend fun login(account: String, password: String): RequestResult<String>

    suspend fun rating(detail: GalleryDetail,rating:Float): RequestResult<RateBack>
}