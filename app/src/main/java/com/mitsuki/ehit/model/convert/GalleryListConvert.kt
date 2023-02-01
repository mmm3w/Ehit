package com.mitsuki.ehit.model.convert

import com.mitsuki.ehit.crutch.network.ehcore.ApiContainer
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.PageInfo
import com.mitsuki.ehit.model.entity.PageInfoNew

class GalleryListConvert : BaseStringConvert<PageInfoNew<Gallery>>() {
    override fun convertResponse(data: String?): PageInfoNew<Gallery> {
        return Gallery.parseExList(data)
    }
}