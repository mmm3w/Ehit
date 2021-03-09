package com.mitsuki.ehit.being.throwable

fun assertContent(content: String?){
    if (content.isNullOrEmpty()) throw ParseThrowable("parsed content is null")
}