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
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository

class GalleryListViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private lateinit var mListPageIn: GalleryListPageIn

    val searchKey: SearchKey
        get() = mListPageIn.searchKey

    val currentKey: String
        get() = mListPageIn.searchKey.key

    val currentType get() = mListPageIn.type

    val searchBarText: MutableLiveData<String> by lazy { MutableLiveData() }

    val searchBarHint: MutableLiveData<String> by lazy { MutableLiveData() }

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

        when (source) {
            GalleryListPageIn.Type.NORMAL,
            GalleryListPageIn.Type.TAG -> {
                searchBarText.postValue(initKey)
                searchBarHint.postValue(string(R.string.hint_search))
            }
            GalleryListPageIn.Type.UPLOADER -> {
                searchBarText.postValue("uploader:$initKey")
                searchBarHint.postValue(string(R.string.hint_search))
            }
            GalleryListPageIn.Type.SUBSCRIPTION -> {
                searchBarText.postValue("")
                searchBarHint.postValue(string(R.string.hint_subscription))
            }
            GalleryListPageIn.Type.WHATS_HOT -> {
                searchBarText.postValue("")
                searchBarHint.postValue(string(R.string.hint_popular))
            }
        }
    }

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page.coerceAtLeast(1)
    }

    fun galleryListCondition(
        searchKey: SearchKey,
        type: GalleryListPageIn.Type = GalleryListPageIn.Type.NORMAL
    ) {
        mListPageIn.type = type
        mListPageIn.searchKey = searchKey
        when (mListPageIn.type) {
            GalleryListPageIn.Type.NORMAL,
            GalleryListPageIn.Type.UPLOADER,
            GalleryListPageIn.Type.TAG -> searchBarText.postValue(searchKey.showContent)
            GalleryListPageIn.Type.SUBSCRIPTION,
            GalleryListPageIn.Type.WHATS_HOT -> {
            }
        }
    }
}