package com.mitsuki.ehit.model.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.pagingsource.FavoritesSource
import com.mitsuki.ehit.model.pagingsource.GalleryDetailImageSource
import com.mitsuki.ehit.model.pagingsource.GalleryListSource
import com.mitsuki.ehit.model.repository.PagingRepository
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    @RemoteRepository val repository: Repository,
) : PagingRepository {

    //page config
    private val mListPagingConfig by lazy { PagingConfig(pageSize = 25) }
    private val mDetailPagingConfig by lazy { PagingConfig(pageSize = 40) }
    private val mFavoritePagingConfig by lazy { PagingConfig(pageSize = 50) }

    override fun galleryList(pageIn: GalleryListPageIn): Flow<PagingData<Gallery>> {
        return Pager(mListPagingConfig, initialKey = GeneralPageIn.START) {
            GalleryListSource(repository, pageIn)
        }.flow
    }

    override fun detailImage(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = GeneralPageIn.START) {
            GalleryDetailImageSource(repository, gid, token, pageIn)
        }.flow
    }

    override fun favoriteList(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): Flow<PagingData<Gallery>> {
        return Pager(mFavoritePagingConfig, initialKey = GeneralPageIn.START) {
            FavoritesSource(repository, pageIn, dataWrap)
        }.flow
    }
}