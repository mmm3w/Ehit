package com.mitsuki.ehit.being.okhttp

import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

class CookieStoreImpl: CookieStore {
    override fun removeAll(): Boolean {
        TODO("Not yet implemented")
    }

    override fun add(uri: URI?, cookie: HttpCookie?) {
        TODO("Not yet implemented")
    }

    override fun getCookies(): MutableList<HttpCookie> {
        TODO("Not yet implemented")
    }

    override fun getURIs(): MutableList<URI> {
        TODO("Not yet implemented")
    }

    override fun remove(uri: URI?, cookie: HttpCookie?): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(uri: URI?): MutableList<HttpCookie> {
        TODO("Not yet implemented")
    }
}