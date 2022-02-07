package com.mitsuki.ehit.viewmodel

import android.os.Bundle

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryListViewModel @Inject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private lateinit var mListPageIn: GalleryListPageIn

    //navigation不支持保存fragment的状态，迟早干掉navigation这个库
    var refreshEnable = false
    var refreshing = false
    var searchBarTranslationY: Float = 0F

    val searchBarText: MutableLiveData<String> by lazy { MutableLiveData() }
    val searchBarHint: MutableLiveData<String> by lazy { MutableLiveData() }
    val pageSource: GalleryPageSource get() = mListPageIn.pageSource
    val maxPage get() = mListPageIn.maxPage

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
        mListPageIn.targetPage = page
    }

    fun galleryListCondition(source: GalleryPageSource) {
        galleryListPage(1)
        mListPageIn.pageSource = source
        searchBarText.postValue(mListPageIn.showContent)
    }
}