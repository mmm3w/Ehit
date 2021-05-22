package com.mitsuki.ehit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository

class GalleryListViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private val mListPageIn = GalleryListPageIn()

    val searchKey: SearchKey?
        get() = mListPageIn.searchKey

    val galleryList: LiveData<PagingData<Gallery>> =
        repository.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
            .asLiveData()

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page.coerceAtLeast(1)
    }

    fun galleryListCondition(searchKey: SearchKey) {
        mListPageIn.searchKey = searchKey
    }
}