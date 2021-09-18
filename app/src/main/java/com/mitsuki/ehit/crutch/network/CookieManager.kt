package com.mitsuki.ehit.crutch.network

import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.db.RoomData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.internal.filterList

object CookieManager {

    private val mMemoryCache: MutableMap<String, Cookie> by lazy { hashMapOf() }

    fun init() {
        //初始化，从数据库中读取到内存
        runBlocking {
            RoomData.cookieDao.queryCookie(ShareData.spDomain).forEach {
                mMemoryCache[it.flag] = it.buildCookie()
            }
        }
    }

    fun new(cookies: List<Cookie>) {
        //换新，清除旧的写入新的
        mMemoryCache.clear()
        for (cookie in cookies) {
            mMemoryCache[cookie.toString()] = cookie
        }
        CoroutineScope(Dispatchers.Default).launch {
            with(RoomData.cookieDao) {
                clearCookie()
                insertCookies(cookies.map { it.c2c() })
            }
        }
    }

    fun clear() {
        // 清除内存缓存
        mMemoryCache.clear()
        // 清除数据库缓存
        CoroutineScope(Dispatchers.Default).launch {
            RoomData.cookieDao.clearCookie()
        }
    }

    fun saveCookie(cookies: List<Cookie>) {
        //内存缓存，使用
        for (cookie in cookies) {
            mMemoryCache[cookie.toString()] = cookie
        }

        CoroutineScope(Dispatchers.Default).launch {
            RoomData.cookieDao.insertCookies(cookies.map { it.c2c() })
        }
    }


    fun loadCookie(url: String): List<Cookie> {
        return mMemoryCache.map { it.value }.filterList { domain == url }
    }

    private fun Cookie.c2c(): com.mitsuki.ehit.model.entity.db.Cookie {
        return com.mitsuki.ehit.model.entity.db.Cookie(name, value, domain, expiresAt, toString())
    }
}