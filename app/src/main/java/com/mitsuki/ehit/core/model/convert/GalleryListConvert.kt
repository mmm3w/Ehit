package com.mitsuki.ehit.core.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.core.model.entity.Gallery
import okhttp3.Response

class GalleryListConvert: Convert <ArrayList<Gallery>>{
    override fun convertResponse(response: Response): ArrayList<Gallery> {
        val webStr = response.body?.string()
        response.close()
        return Gallery.parseList(webStr)
    }
}