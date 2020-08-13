package com.mitsuki.ehit.core.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailItem
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class MainViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    var mCurrentGallery: Gallery? = null

    val galleryList: LiveData<PagingData<Gallery>> = repository.galleryList(0).asLiveData()

    fun galleryDetail(): LiveData<PagingData<GalleryDetailItem>>? = mCurrentGallery?.run {
        return repository.galleryDetail(gid, token).asLiveData()
    }

}