package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.PageInfo
import okhttp3.Response

class ImageSourceConvert : Convert<PageInfo<ImageSource>> {
    override fun convertResponse(response: Response): PageInfo<ImageSource> {
        val webStr = response.body?.string()
        response.close()
        return ImageSource.parse(webStr)
    }
}