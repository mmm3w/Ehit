package com.mitsuki.ehit.viewmodel

import android.content.Intent
import android.os.Bundle

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.GalleryDataMeta
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

    val maxPage get() = mListPageIn.maxPage

    val galleryList: Flow<PagingData<Gallery>> by lazy {
        pagingData.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
    }

    val currentSearchKey: GalleryDataKey?
        get() {
            return when (val meta = mListPageIn.meta) {
                is GalleryDataMeta.Normal,
                is GalleryDataMeta.Subscription -> meta.key
                is GalleryDataMeta.Uploader -> GalleryDataKey("uploader:${meta.name}")
                is GalleryDataMeta.Tag -> GalleryDataKey(meta.tag)
                else -> null
            }
        }

    fun initData(bundle: Bundle?) {
        when (bundle?.getString(DataKey.GALLERY_TYPE_PART)) {
            "watched" -> {
                val sourceIntent =
                    bundle.get("android-support-nav:controller:deepLinkIntent") as? Intent
                val params = sourceIntent?.data?.path ?: ""
                initListPageIn(GalleryDataMeta.Type.SUBSCRIPTION, params)
                return
            }
            "popular" -> {
                initListPageIn(GalleryDataMeta.Type.WHATS_HOT, "")
                return
            }
        }

        val tag = bundle?.getString(DataKey.GALLERY_TYPE_TAG)
        if (!tag.isNullOrEmpty()) {
            initListPageIn(GalleryDataMeta.Type.TAG, tag)
            return
        }

        val uploader = bundle?.getString(DataKey.GALLERY_TYPE_UPLOADER)
        if (!uploader.isNullOrEmpty()) {
            initListPageIn(GalleryDataMeta.Type.UPLOADER, uploader)
            return
        }

        val sourceIntent =
            bundle?.get("android-support-nav:controller:deepLinkIntent") as? Intent
        val params = (sourceIntent?.data?.query ?: "")
            .ifEmpty { bundle?.getString(DataKey.GALLERY_SEARCH_KEY) ?: "" }
        initListPageIn(GalleryDataMeta.Type.NORMAL, params)
    }

    private fun initListPageIn(type: GalleryDataMeta.Type, key: String) {
        mListPageIn = GalleryListPageIn(GalleryDataMeta.create(type, key))
        searchBarText.postValue(mListPageIn.hintContent)
    }

    fun galleryListPage(page: Int) {
        mListPageIn.targetPage = page
    }

    fun galleryListCondition(type: GalleryDataMeta.Type, key: String) {
        galleryListPage(1)
        mListPageIn.meta = GalleryDataMeta.create(type, key)
        searchBarText.postValue(mListPageIn.hintContent)
    }

    fun updateSearchKey(key: GalleryDataKey) {
        galleryListPage(1)
        when (mListPageIn.meta) {
            is GalleryDataMeta.Tag,
            is GalleryDataMeta.Uploader -> mListPageIn.meta = GalleryDataMeta.Normal(key)
            is GalleryDataMeta.Normal,
            is GalleryDataMeta.Subscription -> mListPageIn.updateKey(key)
            else -> {}
        }
        searchBarText.postValue(mListPageIn.hintContent)
    }

}