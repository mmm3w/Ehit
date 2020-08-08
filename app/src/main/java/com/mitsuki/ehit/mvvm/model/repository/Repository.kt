package com.mitsuki.ehit.mvvm.model.repository

import androidx.paging.PagingData
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetailItem
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun galleryList(page: Int): Flow<PagingData<Gallery>>

    fun galleryDetail(gid: Long, token: String): Flow<PagingData<GalleryDetailItem>>
}