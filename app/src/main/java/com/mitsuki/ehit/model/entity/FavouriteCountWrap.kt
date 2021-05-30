package com.mitsuki.ehit.model.entity

import androidx.lifecycle.MutableLiveData

class FavouriteCountWrap {

    val count: MutableLiveData<Array<Int>> by lazy { MutableLiveData() }

    fun postData(data: Array<Int>) {
        count.postValue(data)
    }
}