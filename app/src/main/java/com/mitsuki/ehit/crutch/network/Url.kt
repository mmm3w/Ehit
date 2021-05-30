package com.mitsuki.ehit.crutch.network

import android.content.Context
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData

object Url {

    lateinit var domain: Array<String>

    fun initDomain(context: Context) {
        with(context) {
            domain = arrayOf(getString(R.string.domain_e_ht), getString(R.string.domain_ex_ht))
            if (currentDomain.isEmpty()) currentDomain = domain[0]
        }
    }

    var currentDomain: String
        get() = "https://${ShareData.spDomain}"
        set(value) {
            ShareData.spDomain = value
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