package com.mitsuki.ehit.core.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.SearchKey
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository

class GalleryListViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private val mListPageIn = PageIn()

    val searchKey: SearchKey?
        get() = mListPageIn.searchKey

    val galleryList: LiveData<PagingData<Gallery>> =
        repository.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
            .asLiveData()

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page.coerceAtMost(1)
    }

    fun galleryListCondition(searchKey: SearchKey) {
        mListPageIn.searchKey = searchKey
    }
}