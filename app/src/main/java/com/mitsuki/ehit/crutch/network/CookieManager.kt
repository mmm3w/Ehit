package com.mitsuki.ehit.crutch.network

import com.mitsuki.ehit.crutch.network.site.ApiContainer
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.model.dao.CookieDao
import com.mitsuki.ehit.model.entity.db.CookieCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.internal.filterList
import javax.inject.Inject


class CookieManager @Inject constructor(
    private val cookieDao: CookieDao,
    private val memoryData: MemoryData,
) : CookieJar {

    private val mMemoryCache: MutableList<Cookie> by lazy { arrayListOf() }

    private var mLoginMark: Int = 0

    val isLogin get() = mLoginMark == 7

    init {
        runBlocking {
            mMemoryCache.clear()
            val site = ApiContainer.url
            cookieDao.queryCookie(site).forEach {
                it.buildCookie(site.toHttpUrl())?.apply {
                    loginPick(this)
                    mMemoryCache.add(this)
                }
            }
        }
    }

    fun buildNewCookie(id: String, hash: String, igneous: String) {
        newCookie(
            arrayListOf(
                buildCookie("ipb_member_id", id, "e-hentai.org"),
                buildCookie("ipb_pass_hash", hash, "e-hentai.org"),
                buildCookie("igneous", igneous, "e-hentai.org"),

                buildCookie("ipb_member_id", id, "exhentai.org"),
                buildCookie("ipb_pass_hash", hash, "exhentai.org"),
                buildCookie("igneous", igneous, "exhentai.org")
            )
        )
    }

    fun newCookie(cookies: List<Cookie>) {
        //换新
        mMemoryCache.clear()
        val current = System.currentTimeMillis()
        val newCookies = arrayListOf<Cookie>()
        //合并cookie
        for (cookie in cookies) {
            if (cookie.expiresAt >= current) {
                loginPick(cookie)
                newCookies.add(cookie)
                mMemoryCache.add(cookie)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            cookieDao.clearCookie()
            val site = ApiContainer.url
            cookieDao.insertCookies(newCookies.map {
                CookieCache(
                    domain = site,
                    expires = it.expiresAt,
                    content = it.toString()
                )
            })
        }
    }

    fun clearCookie() {
        mLoginMark = 0
        // 清除内存缓存
        mMemoryCache.clear()
        // 清除数据库缓存
        CoroutineScope(Dispatchers.Default).launch {
            cookieDao.clearCookie()
        }
    }

    fun saveCookie(cookies: List<Cookie>) {
        //清理过期cookie
        clearExpiredCookie()
        val current = System.currentTimeMillis()
        val newCookies = arrayListOf<Cookie>()
        //合并cookie
        for (cookie in cookies) {
            if (cookie.expiresAt >= current) {
                loginPick(cookie)
                newCookies.add(cookie)
                mMemoryCache.add(cookie)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            val site = ApiContainer.url
            cookieDao.insertCookies(newCookies.map {
                CookieCache(
                    domain = site,
                    expires = it.expiresAt,
                    content = it.toString()
                )
            })
        }
    }

    fun loadCookie(url: String): List<Cookie> {
        //是否每次都有清理的必要？
        //clearExpiredCookie()
        return mMemoryCache.filterList { domain == url }
    }

    fun cookieSummary(): Map<String, String> {
        return HashMap<String, String>().apply {
            mMemoryCache.forEach {
                if (it.name == "ipb_member_id") {
                    this["ipb_member_id"] = it.value
                }
                if (it.name == "ipb_pass_hash") {
                    this["ipb_pass_hash"] = it.value
                }
                if (it.name == "igneous") {
                    this["igneous"] = it.value
                }
            }
        }
    }


    fun buildCookie(name: String, value: String, domain: String): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .expiresAt(Long.MAX_VALUE)
            .domain(domain)
            .build()
    }

    private fun clearExpiredCookie() {
        val current = System.currentTimeMillis()
        val mIterator = mMemoryCache.iterator()
        while (mIterator.hasNext()) {
            val next = mIterator.next()
            if (next.expiresAt < current) {
                mIterator.remove()
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            cookieDao.clearExpiredCookie(current)
        }
    }

    private fun loginPick(cookie: Cookie) {
        if (cookie.name == "ipb_member_id") {
            mLoginMark = if (cookie.value.isNotEmpty()) {
                mLoginMark or 1
            } else {
                mLoginMark and 6
            }
            return
        }
        if (cookie.name == "ipb_pass_hash") {
            mLoginMark = if (cookie.value.isNotEmpty()) {
                mLoginMark or 2
            } else {
                mLoginMark and 5
            }
            return
        }
        if (cookie.name == "igneous") {
            mLoginMark = if (cookie.value.isNotEmpty() && cookie.value != "mystery") {
                mLoginMark or 4
            } else {
                mLoginMark and 3
            }
        }
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        //从内存缓存加载 cookie
        return loadCookie(url.host).filterList { name != "yay" }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        saveCookie(cookies)
    }
}