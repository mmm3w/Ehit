package com.mitsuki.ehit.viewmodel

import android.os.Bundle

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.GalleryDataType
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.entity.GalleryDataKey
import com.mitsuki.ehit.model.repository.PagingRepository
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GalleryListViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    var pagingData: PagingRepository,
) : ViewModel() {

    private lateinit var mListPageIn: GalleryListPageIn

    var searchBarTranslationY: Float = 0F

    val refreshEnable: MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    val searchBarText: MutableLiveData<String> by lazy { MutableLiveData() }
    val searchBarHint: MutableLiveData<String> by lazy { MutableLiveData() }

    //    val pageSource: GalleryPageSource get() = mListPageIn.pageSource
    val maxPage get() = mListPageIn.maxPage

    val galleryList: Flow<PagingData<Gallery>> by lazy {
        pagingData.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
    }

    fun initData(bundle: Bundle?) {

        when (bundle?.getString(DataKey.GALLERY_TYPE_PART)) {
            "watched" -> {
                mListPageIn =
                    GalleryListPageIn(GalleryDataType.DEFAULT_SUBSCRIPTION, GalleryDataKey.DEFAULT)
                return
            }
            "popular" -> {
                mListPageIn = GalleryListPageIn(GalleryDataType.DEFAULT_POPULAR, GalleryDataKey.DEFAULT)
                return
            }
        }

        val tag = bundle?.getString(DataKey.GALLERY_TYPE_TAG)
        if (!tag.isNullOrEmpty()) {
            mListPageIn = GalleryListPageIn(GalleryDataType.Tag(tag), GalleryDataKey())
            return
        }

        val uploader = bundle?.getString(DataKey.GALLERY_TYPE_UPLOADER)
        if (!uploader.isNullOrEmpty()){
            mListPageIn = GalleryListPageIn(GalleryDataType.Uploader(uploader), GalleryDataKey.DEFAULT)
            return
        }

        mListPageIn = GalleryListPageIn(GalleryDataType.DEFAULT_NORMAL, GalleryDataKey.DEFAULT)


//        val key = bundle?.getParcelable(DataKey.GALLERY_SEARCH_KEY) ?: DataKey.DEFAULT


//        val source: GalleryPageSource = bundle?.getParcelable(DataKey.GALLERY_PAGE_SOURCE)
//            ?: GalleryPageSource.DEFAULT_NORMAL
//
//        mListPageIn = GalleryListPageIn(source)
//        searchBarText.postValue(source.showContent)
//
//        when (source) {
//            is GalleryPageSource.Normal,
//            is GalleryPageSource.Uploader,
//            is GalleryPageSource.Tag -> searchBarHint.postValue(string(R.string.hint_search))
//            is GalleryPageSource.Subscription -> searchBarHint.postValue(string(R.string.hint_subscription))
//            is GalleryPageSource.Popular -> searchBarHint.postValue(string(R.string.hint_popular))
//        }
    }

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page
    }

    fun galleryListCondition(source: GalleryDataType) {
//        galleryListPage(1)
//        mListPageIn.pageSource = source
//        searchBarText.postValue(mListPageIn.showContent)
    }
}