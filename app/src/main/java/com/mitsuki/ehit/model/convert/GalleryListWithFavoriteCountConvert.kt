package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.model.entity.FavoriteCount
import com.mitsuki.ehit.model.entity.Gallery
import okhttp3.Response

class GalleryListWithFavoriteCountConvert : Convert<Pair<ArrayList<Gallery>, Array<Int>>> {
    override fun convertResponse(response: Response): Pair<ArrayList<Gallery>, Array<Int>> {
        val webStr = response.body?.string()
        response.close()
        return Gallery.parseList(webStr) to FavoriteCount.parse(webStr ?: "")
    }
}