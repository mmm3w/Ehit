package com.mitsuki.ehit.mvvm.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetailItem
import com.mitsuki.ehit.mvvm.model.repository.RemoteRepository
import com.mitsuki.ehit.mvvm.model.repository.RemoteRepositoryImpl
import com.mitsuki.ehit.mvvm.model.repository.Repository
import com.mitsuki.mvvm.base.BaseViewModel

class MainViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    BaseViewModel() {

    var mCurrentGallery: Gallery? = null

    fun galleryList(): LiveData<PagingData<Gallery>> {
        return repository.galleryList(0).asLiveData()
    }

    fun galleryDetail(): LiveData<PagingData<GalleryDetailItem>>? = mCurrentGallery?.run {
        return repository.galleryDetail(gid, token).asLiveData()
    }

}