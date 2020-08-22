package com.mitsuki.ehit.core.model.repository

import androidx.paging.PagingData
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailItem
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun galleryList(pageIn: PageIn): Flow<PagingData<Gallery>>

    fun galleryDetail(gid: Long, token: String): Flow<PagingData<GalleryDetailItem>>
}