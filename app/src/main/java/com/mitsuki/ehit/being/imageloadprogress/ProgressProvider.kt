package com.mitsuki.ehit.being.imageloadprogress

import androidx.lifecycle.*
import kotlinx.coroutines.flow.filter

object ProgressProvider {
    private val mProgressData: MutableLiveData<Progress> = MutableLiveData()
    private val mProgressFlow = mProgressData.asFlow()

    val imageLoadInterceptor = ImageLoadInterceptor(mProgressData)

    fun event(tag: String): LiveData<Progress> {
        return mProgressFlow.filter { it.tag == tag }.asLiveData()
    }
}

fun String.addFeature(tag: String): String {
    val stringBuilder = StringBuilder(this)
    return if (stringBuilder.contains("?")) {
        stringBuilder.append("&")
    } else {
        stringBuilder.append("?")
    }.append("progress=1&")
        .append("tag=$tag").toString()
}

fun String.clearFeature(): Pair<String, String> {
    val start = lastIndexOf("progress=1&") - 1
    if (start < 0) return this to ""
    val tag = substring(start).run { substring(lastIndexOf("tag=")).replace("tag=", "") }
    val realUrl = substring(0, start)
    return realUrl to if (tag.isEmpty()) realUrl else tag
}