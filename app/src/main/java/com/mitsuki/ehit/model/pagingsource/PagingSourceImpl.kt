package com.mitsuki.ehit.model.pagingsource

import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import javax.inject.Inject

class PagingSourceImpl @Inject constructor(val galleryDao: GalleryDao) : PagingSource {

    override fun favoritesSource(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): FavoritesSource {
        return FavoritesSource(pageIn, dataWrap)
    }

    override fun galleryDetailSource(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
        detailSource: GalleryDetailWrap
    ): GalleryDetailSource {
        return GalleryDetailSource(gid, token, pageIn, detailSource, galleryDao)
    }

    override fun galleryListSource(pageIn: GalleryListPageIn): GalleryListSource {
        return GalleryListSource(pageIn)
    }
}