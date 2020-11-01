package com.mitsuki.ehit.being.okhttp

inline fun urlBuilder(base: String, action: UrlParams.() -> Unit) =
    UrlParams(base).apply(action)

class UrlParams(url: String) {
    private val base: StringBuilder = StringBuilder(url)

    fun param(key: String, value: String): UrlParams {
        if (base.contains("?")) {
            base.append("&")
        } else {
            base.append("?")
        }.append(key).append("=").append(value)
        return this
    }

    fun build(): String = base.toString()

    companion object {
        const val LIST_PAGE = "page"
        const val NEW_LOAD = "nl"
    }
}



