package com.mitsuki.ehit.model.pagingsource

import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.repository.Repository

interface PagingSource {

    fun favoritesSource(
        repository: Repository,
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): FavoritesSource

    fun galleryDetailSource(
        repository: Repository,
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
        detailSource: GalleryDetailWrap
    ): GalleryDetailSource

    fun galleryListSource(repository: Repository, pageIn: GalleryListPageIn): GalleryListSource

}