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
import com.mitsuki.ehit.model.page.GalleryListBridge
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

    private lateinit var mBridgeInstance: GalleryListBridge

    /** view states *******************************************************************************/
    //搜索框偏移，仅做缓存，不需要LiveData
    var searchBarTranslationY: Float = 0F

    //刷新控件状态
    private val mSwipeRefreshEnable by lazy { MutableLiveData(false) }
    val swipeRefreshEnable: LiveData<Boolean> get() = mSwipeRefreshEnable

    //跳页按钮
    private val mPagingButtonVisible by lazy { MutableLiveData(false) }
    val pagingButtonVisible: LiveData<Boolean> get() = mPagingButtonVisible

    //收藏按钮
    private val mFavoriteButtonVisible by lazy { MutableLiveData(false) }
    val favoriteButtonVisible: LiveData<Boolean> get() = mPagingButtonVisible

    //搜索是否可用
    private val mSearchEnable by lazy { MutableLiveData(false) }
    val searchEnable: LiveData<Boolean> get() = mSearchEnable

    //搜索框展示的文字
    private val mSearchSummary by lazy { MutableLiveData<String>() }
    val searchSummary: LiveData<String> get() = mSearchSummary


    /**********************************************************************************************/
    //构建页面数据成分
    fun buildSource(bundle: Bundle?) {
        when (bundle?.getString(DataKey.GALLERY_TYPE_PART)) {
            "watched" -> {
                val sourceIntent =
                    bundle.get("android-support-nav:controller:deepLinkIntent") as? Intent
                val params = sourceIntent?.data?.path ?: ""
                val byQuery = bundle.getBoolean(DataKey.GALLERY_SEARCH_KEY_BY_QUERY, true)
                initListPageIn(GalleryDataMeta.Type.SUBSCRIPTION, params, byQuery)
                return
            }
            "popular" -> {
                initListPageIn(GalleryDataMeta.Type.WHATS_HOT, "", false)
                return
            }
        }

        val tag = bundle?.getString(DataKey.GALLERY_TYPE_TAG)
        if (!tag.isNullOrEmpty()) {
            initListPageIn(GalleryDataMeta.Type.TAG, tag, false)
            return
        }

        val uploader = bundle?.getString(DataKey.GALLERY_TYPE_UPLOADER)
        if (!uploader.isNullOrEmpty()) {
            initListPageIn(GalleryDataMeta.Type.UPLOADER, uploader, false)
            return
        }

        val sourceIntent =
            bundle?.get("android-support-nav:controller:deepLinkIntent") as? Intent
        val params = (sourceIntent?.data?.query ?: "")
            .ifEmpty { bundle?.getString(DataKey.GALLERY_SEARCH_KEY) ?: "" }
        val byQuery = bundle?.getBoolean(DataKey.GALLERY_SEARCH_KEY_BY_QUERY, true) ?: true
        initListPageIn(GalleryDataMeta.Type.NORMAL, params, byQuery)
    }


    private fun initListPageIn(type: GalleryDataMeta.Type, key: String, byQuery: Boolean) {
        mBridgeInstance = GalleryListBridge(GalleryDataMeta.create(type, key, byQuery))
        mListPageIn = GalleryListPageIn(GalleryDataMeta.create(type, key, byQuery))
        searchBarText.postValue(mListPageIn.hintContent)
    }


    private lateinit var mListPageIn: GalleryListPageIn


    val refreshEnable: MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    val searchBarText: MutableLiveData<String> by lazy { MutableLiveData() }

//    val enableJump get() = mListPageIn.enableJump
//    val maxPage get() = mListPageIn.maxPage
//
    val galleryList: Flow<PagingData<Gallery>> by lazy {
        pagingData.galleryList(mListPageIn)
            .cachedIn(viewModelScope)
    }
//
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
//
//    val currentDataMeta: GalleryDataMeta get() = mListPageIn.meta
//
//
    fun galleryListPage(
        value: String? = null,
        jump: Boolean = true,
        next: Boolean = true
    ) {

    }

    fun galleryListCondition(type: GalleryDataMeta.Type, key: String) {
        galleryListPage()
        mListPageIn.meta = GalleryDataMeta.create(type, key, false)
        searchBarText.postValue(mListPageIn.hintContent)
    }

    fun updateSearchKey(key: GalleryDataKey) {
        galleryListPage()
        when (mListPageIn.meta) {
            is GalleryDataMeta.Tag,
            is GalleryDataMeta.Uploader -> mListPageIn.meta = GalleryDataMeta.Normal(key)
            is GalleryDataMeta.Normal,
            is GalleryDataMeta.Subscription -> mListPageIn.key = key
            else -> {}
        }
        searchBarText.postValue(mListPageIn.hintContent)
    }

    fun checkPagingAndTriggerDialog() {
        //检查分页信息并弹出窗口
        //从当前页跳转，需要当前页的上一页和下一页，这个怎么拿


    }

}