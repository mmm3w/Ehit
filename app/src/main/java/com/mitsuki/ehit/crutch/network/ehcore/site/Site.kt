package com.mitsuki.ehit.crutch.network.ehcore.site

interface Site {
    val domain: String

    val url: String get() = "https://$domain"

    fun login(): String = "https://forums.e-hentai.org/index.php?act=Login&CODE=01"

    fun galleryList(): String = "$url/"

    fun galleryListBySubscription(): String = "$url/watched"

    fun galleryListByPopular(): String = "$url/popular"

    fun galleryListByTag(key: String): String =
        "$url/tag/${key.trim().replace(" ", "+")}"

    fun galleryListByUploader(name: String): String = "$url/uploader/$name"

    fun galleryDetail(gid: Long, token: String): String =
        "$url/g/$gid/$token"

    fun galleryPreviewDetail(gid: Long, token: String, index: Int): String =
        "$url/s/$token/$gid-$index"

    fun ehSetting(): String = "$url/uconfig.php"

    fun api(): String = "$url/api.php"

    fun favorites(): String = "$url/gallerypopups.php"

    fun favoriteList(): String = "$url/favorites.php"

}