package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository

class GalleryListViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private lateinit var mListPageIn: GalleryListPageIn

    val searchKey: SearchKey?
        get() = mListPageIn.searchKey

    val galleryList: LiveData<PagingData<Gallery>> by lazy {
        repository.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
            .asLiveData()
    }

    fun initData(bundle: Bundle?) {
        val source =
            bundle?.getParcelable(DataKey.GALLERY_LIST_TYPE) ?: GalleryListPageIn.Type.NORMAL
        val initKey = bundle?.getString(DataKey.GALLERY_LIST_INIT_KEY) ?: ""
        mListPageIn = GalleryListPageIn(source, initKey)
    }

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page.coerceAtLeast(1)
    }

    fun galleryListCondition(searchKey: SearchKey) {
        mListPageIn.searchKey = searchKey
    }
}