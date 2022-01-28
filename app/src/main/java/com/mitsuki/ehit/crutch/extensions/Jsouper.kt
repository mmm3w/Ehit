package com.mitsuki.ehit.crutch.extensions

import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import org.jsoup.nodes.Element

fun Element.byClassFirst(className: String, err: String): Element =
    byClassFirstIgnoreError(className) ?: throw ParseThrowable(err)

fun Element.byClassFirstIgnoreError(className: String): Element? =
    getElementsByClass(className)?.first()

fun Element.byTagFirst(tag: String, err: String): Element =
    byTagFirstIgnoreError(tag) ?: throw ParseThrowable(err)

fun Element.byTagFirstIgnoreError(tag: String): Element? =
    getElementsByTag(tag)?.first()