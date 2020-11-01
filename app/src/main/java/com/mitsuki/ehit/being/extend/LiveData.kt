package com.mitsuki.ehit.being.extend

import androidx.annotation.AnyThread
import androidx.lifecycle.MutableLiveData

@AnyThread
inline fun <reified T> MutableLiveData<T>.postNext(map: (T) -> T) {
    value?.apply { postValue(map(this)) }
}