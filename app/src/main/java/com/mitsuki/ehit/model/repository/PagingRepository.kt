package com.mitsuki.ehit.model.repository

import androidx.paging.PagingData
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import kotlinx.coroutines.flow.Flow

interface PagingRepository {

    fun galleryList(pageIn: GalleryListPageIn): Flow<PagingData<Gallery>>

    fun detailImage(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn
    ): Flow<PagingData<ImageSource>>

    fun favoriteList(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): Flow<PagingData<Gallery>>
}