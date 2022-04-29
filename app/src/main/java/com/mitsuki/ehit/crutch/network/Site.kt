package com.mitsuki.ehit.crutch.network

object Site {

    const val EH = "e-hentai.org" //0
    const val EX = "exhentai.org" //1

    var currentDomain: String = domain(0)
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    fun host(id: Int): String {
        return if (id == 1) EX else EH
    }

    fun domain(id: Int): String {
        return "https://${host(id)}"
    }

    fun refreshDomain(id: Int) {
        currentDomain = domain(id)
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