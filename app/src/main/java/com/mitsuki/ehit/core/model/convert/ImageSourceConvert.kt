package com.mitsuki.ehit.core.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.model.entity.PageInfo
import okhttp3.Response

class ImageSourceConvert : Convert<PageInfo<ImageSource>> {
    override fun convertResponse(response: Response): PageInfo<ImageSource>? {
        val webStr = response.body?.string()
        response.close()
        return ImageSource.parse(webStr)
    }
}