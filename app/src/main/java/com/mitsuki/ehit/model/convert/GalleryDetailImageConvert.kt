package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.model.entity.GalleryDetail
import com.mitsuki.ehit.model.entity.PageInfo
import com.mitsuki.ehit.model.entity.ImageSource
import okhttp3.Response

class GalleryDetailImageConvert : Convert<Pair<GalleryDetail, PageInfo<ImageSource>>> {
    override fun convertResponse(response: Response): Pair<GalleryDetail, PageInfo<ImageSource>> {
        val webStr = response.body?.string()
        response.close()
        val detail = GalleryDetail.parse(webStr)
        val images = ImageSource.parse(webStr)
        return detail to images
    }
}