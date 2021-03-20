package com.mitsuki.ehit.being.network

import android.util.Base64
import com.mitsuki.ehit.being.ShareData
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class CookieJarImpl(private val cache: ShareData) : CookieJar {

    private val mMemoryCache: MutableMap<String, Cookie> by lazy { hashMapOf() }

    init {
        refresh()
    }

    fun refresh() {
        val cookiesNames = cache.spCookies.split(",")
        for (name in cookiesNames) {
            cache.string(name).toCookie()?.apply { mMemoryCache[name] = this }
        }
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        cache.edit {
            with(mMemoryCache.iterator()) {
                while (hasNext()) {
                    next().apply {
                        if (value.expiresAt < System.currentTimeMillis()) {
                            remove()
                            remove(key)
                        }
                    }
                }
            }
        }
        return mMemoryCache.map { it.value }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cache.edit {
            var cookieName = ""
            for (cookie in cookies) {
                if (cookie.expiresAt < System.currentTimeMillis() || !cookie.persistent) {
                    remove(cookie.name)
                } else {
                    mMemoryCache[cookie.name] = cookie
                    putString(cookie.name, cookie.base64())
                    cookieName = cookieName + cookie.name + ","
                }
            }
            if (cookieName.isNotEmpty()) {
                with(cache.string(ShareData.SP_COOKIES)) {
                    if (isNotEmpty()) {
                        putString(ShareData.SP_COOKIES, cookieName + this)
                    } else {
                        putString(
                            ShareData.SP_COOKIES,
                            cookieName.substring(0, cookieName.length - 1)
                        )
                    }
                }
            }
        }
    }


    companion object {
        fun newBase64Cookie(name: String, value: String): String {
            return Cookie.Builder()
                .name(name)
                .value(value)
                .expiresAt(Long.MAX_VALUE)
                .domain(ShareData.spDomain)
                .build()
                .base64()
        }

        private fun Cookie.base64(): String {
            val bytes = ByteArrayOutputStream().let { byteArrayOutputStream ->
                ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
                    objectOutputStream.writeObject(SerializableCookie(this))
                    byteArrayOutputStream.toByteArray()
                }
            }
            return try {
                Base64.encodeToString(bytes, Base64.DEFAULT)
            } catch (inner: Throwable) {
                ""
            }
        }

        private fun String.toCookie(): Cookie? {
            return try {
                val bytes = Base64.decode(this, Base64.DEFAULT)
                ByteArrayInputStream(bytes).let { byteArrayInputStream ->
                    ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
                        (objectInputStream.readObject() as? SerializableCookie)?.cookie()
                    }
                }
            } catch (inner: Throwable) {
                null
            }
        }
    }


}