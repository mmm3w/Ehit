package com.mitsuki.ehit.core.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryListSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val requestProvider: RequestProvider
) : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25)

    private val mDetailPagingConfig =
        PagingConfig(pageSize = 40)

    override fun galleryList(pageIn: PageIn): Flow<PagingData<Gallery>> {
        return Pager(mListPagingConfig, initialKey = 0) {
            GalleryListSource(pageIn, requestProvider)
        }.flow
    }

    override fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: PageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = 0) {
            GalleryDetailSource(gid, token, pageIn, detailSource, requestProvider)
        }.flow
    }


}