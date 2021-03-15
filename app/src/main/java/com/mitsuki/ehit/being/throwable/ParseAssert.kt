package com.mitsuki.ehit.being.throwable

fun assertContent(content: String?, err: String) {
    if (content.isNullOrEmpty()) throw ParseThrowable(err)
}