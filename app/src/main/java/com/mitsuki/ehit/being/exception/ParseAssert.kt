package com.mitsuki.ehit.being.exception

fun assertContent(content: String?){
    if (content.isNullOrEmpty()) throw ParseException("parsed content is null")
}