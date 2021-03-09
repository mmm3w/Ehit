package com.mitsuki.ehit.core.model.ehparser

import com.mitsuki.ehit.being.throwable.ParseThrowable
import org.jsoup.nodes.Element

fun Element.byClassFirst(className: String, err: String): Element =
    getElementsByClass(className)?.first() ?: throw ParseThrowable(err)

fun Element.byTagFirst(tag: String, err: String): Element =
    getElementsByTag(tag)?.first() ?: throw ParseThrowable(err)