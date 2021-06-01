package com.mitsuki.ehit.model.entity

import androidx.lifecycle.MutableLiveData

class FavouriteCountWrap {

    val count: MutableLiveData<Array<Pair<String, Int>>> by lazy { MutableLiveData() }

    fun postData(data: Array<Pair<String, Int>>) {
        count.postValue(data)
    }
}