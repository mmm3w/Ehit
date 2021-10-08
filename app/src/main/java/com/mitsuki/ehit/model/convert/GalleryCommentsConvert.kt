package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.model.entity.GalleryDetail
import com.mitsuki.ehit.model.entity.PageInfo
import com.mitsuki.ehit.model.entity.ImageSource
import okhttp3.Response

class GalleryCommentsConvert : Convert<List<Comment>> {
    override fun convertResponse(response: Response): List<Comment> {
        val webStr = response.body?.string()
        response.close()
        val detail = GalleryDetail.parse(webStr)
        return detail.comments.toList()
    }
}