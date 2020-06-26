package com.mitsuki.ehit.mvvm.model.ehparser

import android.text.TextUtils
import org.jsoup.nodes.Element
import java.util.regex.Pattern

val TI_MATCHER = Pattern.compile("(\\d+)/([0-9a-f]{10})(?:[^0-9a-f]|\$)")
val RATING_MATCHER = Pattern.compile("(\\-?\\d+)px (\\-?\\d+)px")

fun Element.byClassFirst(className: String, msg: String = ""): Element {
    return getElementsByClass(className)?.first() ?: throw ParseException(msg)
}

fun Element.byId(id: String, msg: String = ""): Element {
    return getElementById(id).firstElementSibling() ?: throw ParseException(msg)
}

fun Element.byTagFirst(tag: String, msg: String = ""): Element {
    return getElementsByTag(tag)?.first() ?: throw ParseException(msg)
}

fun String.splitTI(): Array<String> {
    if (TextUtils.isEmpty(this)) throw ParseException("url is null")
    TI_MATCHER.matcher(this).let {
        if (it.find()) {
            return arrayOf(it.group(1), it.group(2))
        } else throw ParseException("not found params")
    }
}

fun String.parseRating(): Float {
    if (TextUtils.isEmpty(this)) return -1f
    RATING_MATCHER.matcher(this).let {
        return if (it.find()) {
            var rating = 5f
            rating += it.group(1).toFloat() / 16
            rating += if (it.group(2).toFloat() == -21f) -0.5f else 0f
            rating
        } else -1f
    }
}