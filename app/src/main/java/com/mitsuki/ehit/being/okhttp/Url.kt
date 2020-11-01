package com.mitsuki.ehit.being.okhttp

object Url {
    val HENTAI_E = "https://e-hentai.org"
    val HENTAI_EX = "https://exhentai.org"

    fun host(): String {
        return "$HENTAI_E/"
    }

    fun list(): String {
        return host()
    }

    //页码从0开始，页码是0的时候不追加参数
    fun galleryList(page: Int): String {
        return urlBuilder(host()) {
            if (page > 0)
                param(UrlParams.LIST_PAGE, page.toString())
        }.build()
    }

    fun galleryDetail(gid: Long, token: String, index: Int = 0, hc: Boolean = false): String {
        return urlBuilder("${host()}g/$gid/$token/") {
            if (index != 0)
                param("p", index.toString())
            if (hc)
                param("hc", "1")
        }.build()
    }

    fun galleryPreviewDetail(gid: Long, token: String, index: Int = 0, nl: String? = null): String {
        return urlBuilder("${host()}s/$token/$gid-${index + 1}") {
            if (!nl.isNullOrEmpty())
                param(UrlParams.NEW_LOAD, nl)
        }.build()
    }
}