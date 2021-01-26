package com.mitsuki.ehit.being.network

object Url {
    const val HENTAI_E = "https://e-hentai.org"
    const val HENTAI_EX = "https://exhentai.org"

    private val host: String
        get() = "$HENTAI_E/"

    fun host(): String {
        return host
    }

    fun login(): String {
        return "https://forums.e-hentai.org/index.php?act=Login&CODE=01"
    }

    fun galleryList(): String {
        return host()
    }

    fun galleryDetail(gid: Long, token: String): String {
        return "${host()}g/$gid/$token"
    }

    fun galleryPreviewDetail(gid: Long, token: String, index: Int): String {
        return "${host()}s/$token/$gid-${index + 1}"
    }

}