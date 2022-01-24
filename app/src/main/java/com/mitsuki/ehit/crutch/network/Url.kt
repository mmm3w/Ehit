package com.mitsuki.ehit.crutch.network

import android.content.Context
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData

object Url {

    const val EH = "e-hentai.org"
    const val EX = "exhentai.org"

    var domain: Array<String> = arrayOf(EH, EX)

    //由link跳转进来应该需要按link的domain调整整体的domain

    var proxyDomain: String? = null

    var domainCache: String = ""

    val currentDomain: String
        get() {
            if (proxyDomain != null && proxyDomain?.isNotEmpty() == true) {
                return "https://${proxyDomain}"
            }
            return "https://${domainCache}"
        }



    val login: String get() = "https://forums.e-hentai.org/index.php?act=Login&CODE=01"

    val galleryList: String get() = "$currentDomain/"

    val galleryListBySubscription: String get() = "$currentDomain/watched"

    val galleryListByPopular: String get() = "$currentDomain/popular"

    fun galleryListByTag(key: String): String = "$currentDomain/tag/$key"

    fun galleryListByUploader(name: String): String = "$currentDomain/uploader/$name"

    fun galleryDetail(gid: Long, token: String): String = "$currentDomain/g/$gid/$token"

    fun galleryPreviewDetail(gid: Long, token: String, index: Int): String =
        "$currentDomain/s/$token/$gid-${index + 1}"

    val ehSetting: String get() = "$currentDomain/uconfig.php"

    val api: String get() = "$currentDomain/api.php"

    val favorites: String get() = "$currentDomain/gallerypopups.php"

    val favoriteList: String get() = "$currentDomain/favorites.php"


}