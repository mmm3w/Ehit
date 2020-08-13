@file:Suppress(
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)

package com.mitsuki.ehit.core.model.ehparser

import android.text.TextUtils
import org.jsoup.nodes.Element
import java.lang.Exception

/**************************************************************************************************/
fun Element.byClassFirst(className: String, msg: String = ""): Element {
    return getElementsByClass(className)?.first() ?: throw ParseException(msg)
}

fun Element.byId(id: String, msg: String = ""): Element {
    return getElementById(id) ?: throw ParseException(msg)
}

fun Element.byTagFirst(tag: String, msg: String = ""): Element {
    return getElementsByTag(tag)?.first() ?: throw ParseException(msg)
}

/**************************************************************************************************/
fun String.htmlEscape(): String {
    return replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#039;", "'")
        .replace("&times;", "×")
        .replace("&nbsp;", " ")
}

fun String.matchNumber(default: String = "0"): String {
    return Matcher.NUMBER.matcher(this).let {
        try {
            if (it.find()) it.group(1) else default
        }catch (e:Exception){
            default
        }
    }
}

/**************************************************************************************************/
fun String.splitIdToken(): Array<String> {
    if (TextUtils.isEmpty(this)) throw ParseException("url is null")
    Matcher.ID_TOKEN.matcher(this).let {
        if (it.find()) {
            return arrayOf(it.group(1), it.group(2))
        } else throw ParseException("not found params")
    }
}

fun String.parseRating(): Float {
    if (TextUtils.isEmpty(this)) return -1f
    Matcher.RATING.matcher(this).let {
        return if (it.find()) {
            var rating = 5f
            rating += it.group(1).toFloat() / 16
            rating += if (it.group(2).toFloat() == -21f) -0.5f else 0f
            rating
        } else -1f
    }
}

fun String.parseDetail(): Array<String> {
    Matcher.DETAIL.matcher(this).let {
        if (it.find()) {
            return arrayOf(it.group(1), it.group(2), it.group(3), it.group(4))
        } else throw ParseException("not found detail")
    }
}

fun String.parseTorrent(): Array<String> {
    Matcher.TORRENT.matcher(this).let {
        return if (it.find()) {
            arrayOf(it.group(1), it.group(2))
        } else arrayOf("", "0")
    }
}

fun String.parseArchive(): String {
    Matcher.ARCHIVE.matcher(this).let {
        return if (it.find()) {
            it.group(1)
        } else ""
    }
}

fun String.parseDetailThumb(): String {
    Matcher.DETAIL_COVER.matcher(this).let {
        if (it.find()) {
            return it.group(3)
        } else throw ParseException("not found detail thumb")
    }
}
