package com.mitsuki.ehit.being.loadprogress

import com.mitsuki.ehit.being.extend.hideWithMainThread
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

object ProgressProvider {

    private const val URL_MARK = "MTKMark="

    private val mProgressSubject: PublishSubject<Progress> by lazy { PublishSubject.create() }

    val imageLoadInterceptor = LoadInterceptor(mProgressSubject)

    fun event(tag: String): Observable<Progress> = mProgressSubject.hideWithMainThread().filter { it.tag == tag }

    fun decorateUrl(url: String, tag: String): String {
        val stringBuilder = StringBuilder(url)
        return if (stringBuilder.contains("?")) {
            stringBuilder.append("&")
        } else {
            stringBuilder.append("?")
        }.append("$URL_MARK$tag").toString()
    }

    fun cleanUrl(url: String): Pair<String, String> {
        return if (url.contains(URL_MARK))
            url.substring(0, url.indexOf(URL_MARK) - 1) to url.substring(url.indexOf(URL_MARK))
                .replace(URL_MARK, "")
        else
            url to ""
    }
}

fun String.addFeature(tag: String): String = ProgressProvider.decorateUrl(this, tag)

fun String.clearFeature(): Pair<String, String> = ProgressProvider.cleanUrl(this)