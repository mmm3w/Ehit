package com.mitsuki.ehit.core.model.repository

import androidx.paging.PagingData
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailItem
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun galleryList(page: Int): Flow<PagingData<Gallery>>

    fun galleryDetail(gid: Long, token: String): Flow<PagingData<GalleryDetailItem>>
}