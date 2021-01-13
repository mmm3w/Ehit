package com.mitsuki.ehit.core.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.core.model.entity.GalleryPreview
import okhttp3.Response

class GalleryPreviewConvert : Convert<GalleryPreview> {
    override fun convertResponse(response: Response): GalleryPreview? {
        val webStr = response.body?.string()
        response.close()
        return GalleryPreview.parse(webStr)
    }
}