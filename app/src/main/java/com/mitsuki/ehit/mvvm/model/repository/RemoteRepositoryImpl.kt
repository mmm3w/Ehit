package com.mitsuki.ehit.mvvm.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetailItem
import com.mitsuki.ehit.mvvm.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.mvvm.model.pagingsource.GalleryListSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val pagingConfig: PagingConfig,
    private val requestProvider: RequestProvider
) : Repository {

    override fun galleryList(page: Int): Flow<PagingData<Gallery>> {
        return Pager(pagingConfig) { GalleryListSource(requestProvider) }.flow
    }

    override fun galleryDetail(gid: Long, token: String): Flow<PagingData<GalleryDetailItem>> {
        return Pager(pagingConfig) { GalleryDetailSource(gid, token, requestProvider) }.flow
    }


}