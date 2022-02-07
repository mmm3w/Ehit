package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.PageInfo
import okhttp3.Response

class GalleryListWithFavoriteCountConvert : Convert<Pair<PageInfo<Gallery>, Array<Int>>> {
    override fun convertResponse(response: Response): Pair<PageInfo<Gallery>, Array<Int>> {
        val webStr = response.body?.string()
        response.close()
        return Gallery.parseList(webStr) to GalleryFavorites.parse(webStr ?: "")
    }
}