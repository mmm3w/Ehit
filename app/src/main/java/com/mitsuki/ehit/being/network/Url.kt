package com.mitsuki.ehit.being.network

object Url {
    const val HENTAI_E = "https://e-hentai.org"
    const val HENTAI_EX = "https://exhentai.org"

    const val PAGE_LIST = "page"
    const val NEW_LOAD = "nl"
    const val PAGE_DETAIL = "p"

    const val USER_NAME = "UserName"
    const val PASS_WORD = "PassWord"
    const val COOKIE_DATE = "CookieDate"
    const val B = "b"
    const val BT = "bt"
    const val REFERER = "referer"
    const val PRIVACY = "Privacy"
    const val SUBMIT = "submit"

    private val host: String
        get() = "$HENTAI_EX/"

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