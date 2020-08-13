package com.mitsuki.ehit.core.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailItem
import com.mitsuki.ehit.core.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryListSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val requestProvider: RequestProvider
) : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25, initialLoadSize = 25, prefetchDistance = 2)

    override fun galleryList(page: Int): Flow<PagingData<Gallery>> {
        return Pager(mListPagingConfig) { GalleryListSource(requestProvider) }.flow
    }

    override fun galleryDetail(gid: Long, token: String): Flow<PagingData<GalleryDetailItem>> {
        return Pager(mListPagingConfig) { GalleryDetailSource(gid, token, requestProvider) }.flow
    }


}