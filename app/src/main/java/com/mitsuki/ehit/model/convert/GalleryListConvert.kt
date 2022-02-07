package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.PageInfo
import okhttp3.Response

class GalleryListConvert: Convert <PageInfo<Gallery>>{
    override fun convertResponse(response: Response): PageInfo<Gallery> {
        val webStr = response.body?.string()
        response.close()
        return Gallery.parseList(webStr)
    }
}