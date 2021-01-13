package com.mitsuki.ehit.core.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.core.model.entity.GalleryDetail
import okhttp3.Response

class GalleryDetailConvert : Convert<GalleryDetail> {
    override fun convertResponse(response: Response): GalleryDetail? {
        val webStr = response.body?.string()
        response.close()
        return GalleryDetail.parse(webStr)
    }
}