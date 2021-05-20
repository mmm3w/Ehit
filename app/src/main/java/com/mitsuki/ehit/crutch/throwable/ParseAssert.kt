package com.mitsuki.ehit.crutch.throwable

fun assertContent(content: String?, err: String) {
    if (content.isNullOrEmpty()) throw ParseThrowable(err)
}