package com.mitsuki.ehit.being

object Url {
    val HENTAI_E = "https://e-hentai.org"
    val HENTAI_EX = "https://exhentai.org"

    fun host(): String {
        return "$HENTAI_E/"
    }

    fun list(): String {
        return host()
    }

    fun galleryDetail(gid: Long, token: String, index: Int = 0, hc: Boolean = false): String {
        val url = StringBuilder("${host()}g/$gid/$token/")
        if (index != 0) url.param("p", index.toString())
        if (hc) url.param("hc", "1")
        return url.toString()
    }
}

fun StringBuilder.param(key: String, value: String): StringBuilder {
    return if (this.contains("?")) {
        append("&")
    } else {
        append("?")
    }.append(key).append("=").append(value)
}