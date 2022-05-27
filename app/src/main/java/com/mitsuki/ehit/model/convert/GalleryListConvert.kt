package com.mitsuki.ehit.model.convert

import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.PageInfo

class GalleryListConvert : BaseStringConvert<PageInfo<Gallery>>() {
    override fun convertResponse(data: String?): PageInfo<Gallery> {
        return Gallery.parseList(data)
    }
}