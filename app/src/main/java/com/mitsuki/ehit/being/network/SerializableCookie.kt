package com.mitsuki.ehit.being.network

import okhttp3.Cookie
import java.io.Serializable

data class SerializableCookie(
    val name: String,
    val value: String,
    val expiresAt: Long,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val httpOnly: Boolean,
    val persistent: Boolean,
    val hostOnly: Boolean
) : Serializable {
    constructor(cookie: Cookie) : this(
        cookie.name,
        cookie.value,
        cookie.expiresAt,
        cookie.domain,
        cookie.path,
        cookie.secure,
        cookie.httpOnly,
        cookie.persistent,
        cookie.hostOnly
    )

    fun cookie(): Cookie {
        return Cookie.Builder().apply {
            name(name)
            value(value)
            expiresAt(expiresAt)
            domain(domain)
            path(path)
            if (secure) secure()
            if (httpOnly) httpOnly()
            if (hostOnly) hostOnlyDomain(domain) else domain(domain)
        }.build()
    }
}