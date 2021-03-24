package com.mitsuki.ehit.being

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

object MoshiProvider {
    val moshi by lazy {
        Moshi.Builder().build()
    }

    fun <T> adapter(type: Class<T>): JsonAdapter<T> {
        return moshi.adapter(type)
    }
}

inline fun <reified T> T.toJson(): String {
    return MoshiProvider.adapter(T::class.java).toJson(this)
}

inline fun <reified T> String.fromJson(): T? {
    return MoshiProvider.adapter(T::class.java).fromJson(this)
}