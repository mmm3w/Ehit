package com.mitsuki.ehit.model.pagingsource

import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.repository.Repository
import javax.inject.Inject

class PagingSourceImpl @Inject constructor() :
    PagingSource {

    override fun galleryListSource(
        repository: Repository,
        pageIn: GalleryListPageIn
    ): GalleryListSource {
        return GalleryListSource(repository, pageIn)
    }

    override fun favoritesSource(
        repository: Repository,
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): FavoritesSource {
        return FavoritesSource(repository, pageIn, dataWrap)
    }

    override fun detailImageSource(
        repository: Repository,
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
    ): GalleryDetailImageSource {
        return GalleryDetailImageSource(repository, gid, token, pageIn)
    }


}