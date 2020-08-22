package com.mitsuki.ehit.core.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailItem
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository

class MainViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    var mCurrentGallery: Gallery? = null

    private val mListPageIn = PageIn()

    val galleryList: LiveData<PagingData<Gallery>> =
        repository.galleryList(mListPageIn).asLiveData()

    fun galleryListPage(page: Int) {
        mListPageIn.jump(page)
    }

    fun galleryDetail(): LiveData<PagingData<GalleryDetailItem>>? = mCurrentGallery?.run {
        return repository.galleryDetail(gid, token).asLiveData()
    }

}