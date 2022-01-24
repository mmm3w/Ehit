package com.mitsuki.ehit.crutch.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.internal.filterList
import javax.inject.Inject

class CookieJarImpl @Inject constructor(val manager: CookieManager) : CookieJar {

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        //从内存缓存加载 cookie
        return manager.loadCookie(url.host).filterList { name != "yay" }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        manager.saveCookie(cookies)
    }
}