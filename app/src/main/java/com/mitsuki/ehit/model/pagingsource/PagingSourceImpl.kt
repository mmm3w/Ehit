package com.mitsuki.ehit.model.pagingsource

import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.repository.Repository
import javax.inject.Inject

class PagingSourceImpl @Inject constructor(val galleryDao: GalleryDao) :
    PagingSource {

    override fun favoritesSource(
        repository: Repository,
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): FavoritesSource {
        return FavoritesSource(repository, pageIn, dataWrap)
    }

    override fun galleryDetailSource(
        repository: Repository,
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
        detailSource: GalleryDetailWrap
    ): GalleryDetailSource {
        return GalleryDetailSource(repository, galleryDao, gid, token, pageIn, detailSource)
    }

    override fun galleryListSource(
        repository: Repository,
        pageIn: GalleryListPageIn
    ): GalleryListSource {
        return GalleryListSource(repository, pageIn)
    }
}