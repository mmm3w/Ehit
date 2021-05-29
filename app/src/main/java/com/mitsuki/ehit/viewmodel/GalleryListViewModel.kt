package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository

class GalleryListViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private lateinit var mListPageIn: GalleryListPageIn

    val searchBarText: MutableLiveData<String> by lazy { MutableLiveData() }
    val searchBarHint: MutableLiveData<String> by lazy { MutableLiveData() }
    val pageSource: GalleryPageSource get() = mListPageIn.pageSource

    val galleryList: LiveData<PagingData<Gallery>> by lazy {
        repository.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
            .asLiveData()
    }

    fun initData(bundle: Bundle?) {
        val source: GalleryPageSource = bundle?.getParcelable(DataKey.GALLERY_PAGE_SOURCE)
            ?: GalleryPageSource.DEFAULT_NORMAL

        mListPageIn = GalleryListPageIn(source)
        searchBarText.postValue(source.showContent)

        when (source) {
            is GalleryPageSource.Normal,
            is GalleryPageSource.Uploader,
            is GalleryPageSource.Tag -> searchBarHint.postValue(string(R.string.hint_search))
            is GalleryPageSource.Subscription -> searchBarHint.postValue(string(R.string.hint_subscription))
            is GalleryPageSource.Popular -> searchBarHint.postValue(string(R.string.hint_popular))
        }
    }

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page.coerceAtLeast(1)
    }

    fun galleryListCondition(source: GalleryPageSource) {
        galleryListPage(1)
        mListPageIn.pageSource = source
        searchBarText.postValue(mListPageIn.showContent)
    }
}