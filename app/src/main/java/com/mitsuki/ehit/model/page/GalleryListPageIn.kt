package com.mitsuki.ehit.model.page

import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.model.entity.SearchKey
import java.lang.RuntimeException

class GalleryListPageIn {
    companion object{
        const val START = 0
    }

    var type: Type = Type.MAIN

    //0为起始第一页
    var targetPage: Int = START
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