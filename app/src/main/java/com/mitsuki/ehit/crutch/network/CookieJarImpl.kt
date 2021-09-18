package com.mitsuki.ehit.crutch.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.internal.filterList

class CookieJarImpl : CookieJar {

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        //从内存缓存加载 cookie
        return CookieManager.loadCookie(url.host).filterList { name != "yay" }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        CookieManager.saveCookie(cookies)
    }
}