package com.mitsuki.ehit.model.pagingsource

import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn

interface PagingSource {

    fun favoritesSource(pageIn: FavouritePageIn, dataWrap: FavouriteCountWrap): FavoritesSource

    fun galleryDetailSource(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
        detailSource: GalleryDetailWrap
    ): GalleryDetailSource

    fun galleryListSource(pageIn: GalleryListPageIn): GalleryListSource

}