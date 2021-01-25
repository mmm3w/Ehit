package com.mitsuki.ehit.core.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.core.model.entity.SearchKey

class MainViewModel @ViewModelInject constructor() : ViewModel() {

    private val mSearchKey: MutableMap<Int, MutableLiveData<SearchKey>> = hashMapOf()

    fun postSearchKey(code: Int, key: SearchKey) {
        (mSearchKey[code] ?: MutableLiveData<SearchKey>().apply { mSearchKey[code] = this })
            .postValue(key)
    }

    fun searchKey(code: Int): LiveData<SearchKey> {
        return mSearchKey[code] ?: MutableLiveData<SearchKey>().apply { mSearchKey[code] = this }
    }

    fun removeSearchKey(code: Int) {
        mSearchKey.remove(code)
    }

}