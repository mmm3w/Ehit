package com.mitsuki.ehit.model.ehparser

import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import org.jsoup.nodes.Element

fun Element.byClassFirst(className: String, err: String): Element =
    getElementsByClass(className)?.first() ?: throw ParseThrowable(err)

fun Element.byTagFirst(tag: String, err: String): Element =
    getElementsByTag(tag)?.first() ?: throw ParseThrowable(err)