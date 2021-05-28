package com.mitsuki.ehit.model.page

import android.os.Parcelable
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.model.entity.SearchKey
import kotlinx.parcelize.Parcelize
import java.lang.RuntimeException

class GalleryListPageIn(t: Type, private val initKey: String) {
    companion object {
        const val START = 0
    }

    var type: Type = t

    //0为起始第一页
    var targetPage: Int = START
        set(value) {
            if (value < 1) throw  RuntimeException("Value error")
            field = value - 1
        }

    var searchKey: SearchKey? =
        if (t == Type.NORMAL && initKey.isNotEmpty()) SearchKey(key = initKey) else null
        set(value) {
            if (type == Type.TAG || type == Type.UPLOADER) type = Type.NORMAL
            field = value
        }


    val targetUrl: String
        get() = when (type) {
            Type.NORMAL -> Url.galleryList
            Type.UPLOADER -> Url.galleryListByUploader(initKey)
            Type.TAG -> Url.galleryListByTag(initKey)
            Type.SUBSCRIPTION -> Url.galleryListBySubscription
            Type.WHATS_HOT -> Url.galleryListByPopular
        }


    fun addPage(source: UrlParams, index: Int) {
        if (index == START) return
        if (type != Type.WHATS_HOT) source.urlParams(RequestKey.PAGE, index.toString())
    }

    fun addSearchKey(source: UrlParams) {
        if (type == Type.NORMAL || type == Type.SUBSCRIPTION)
            searchKey?.addParams(source)
    }

    fun docerPrevKey(key: Int?): Int? {
        if (type == Type.WHATS_HOT) return null
        return key
    }

    fun docerNextKey(key: Int?): Int? {
        if (type == Type.WHATS_HOT) return null
        return key
    }

    @Parcelize
    enum class Type : Parcelable {
        NORMAL, //仅显示key
        UPLOADER, //转到normal uploader:name
        TAG, //转到normal taggroup:tagname
        SUBSCRIPTION, //内部搜索 订阅
        WHATS_HOT //没有搜索 显示热点并禁用事件


        /*
        * 关于搜索有类型，
        * 搜索的类型 加上key
        * 不考虑高级筛选
        *
        *
        * */


    }
}