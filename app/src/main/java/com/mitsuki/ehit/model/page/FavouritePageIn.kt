package com.mitsuki.ehit.model.page

import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams

class FavouritePageIn: GeneralPageIn() {

    var group:Int = -1

    fun setGroup(source: UrlParams) {
        if (group == -1) return
        source.urlParams("favcat", group.toString())
    }
}