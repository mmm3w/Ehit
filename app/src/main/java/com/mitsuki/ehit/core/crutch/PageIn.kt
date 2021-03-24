package com.mitsuki.ehit.core.crutch

import com.mitsuki.ehit.being.network.Url
import com.mitsuki.ehit.core.model.entity.SearchKey
import java.lang.RuntimeException

class PageIn {

    var type: Type = Type.MAIN

    //0为起始第一页
    var targetPage: Int = 0
        set(value) {
            if (value < 1) throw  RuntimeException("Value error")
            field = value - 1
        }

    var searchKey: SearchKey? = null

    val targetUrl: String
        get() = when (type) {
            Type.MAIN -> Url.galleryList
            Type.SUBSCRIPTION -> Url.galleryListBySubscription
            Type.WHATS_HOT -> Url.galleryListByPopular
        }

    enum class Type {
        MAIN, SUBSCRIPTION, WHATS_HOT
    }
}