package com.mitsuki.ehit.mvvm.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import androidx.paging.PagingConfig
import com.mitsuki.ehit.mvvm.model.repository.MainModel
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.mvvm.base.BaseViewModel

class MainViewModel @ViewModelInject constructor(model: MainModel) :
    BaseViewModel<MainModel>(model) {

    private val pagingConfig = PagingConfig(
        pageSize = 25
    )

    var mCurrentGallery: Gallery? = null

    suspend fun galleryList() = model.galleryList(pagingConfig).asLiveData()

    suspend fun galleryDetail() = mCurrentGallery?.run {
//        model.galleryDetail(1409189, "59c1bb0d20").asLiveData()
        model.galleryDetail(gid, token).asLiveData()
    }

}