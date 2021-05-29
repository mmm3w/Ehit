package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import coil.memory.MemoryCache
import com.mitsuki.armory.adapter.NotifyItem
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.entity.HeaderInfo
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.GalleryDetailPageIn
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

class GalleryDetailViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    lateinit var baseInfo: Gallery
    private val mDetailPageIn = GalleryDetailPageIn()

    val infoWrap = GalleryDetailWrap()

    val toastData: SingleLiveEvent<String> by lazy { SingleLiveEvent() }
    val rateNotify: SingleLiveEvent<NotifyItem> by lazy { SingleLiveEvent() }
    val favNotify: SingleLiveEvent<String> by lazy { SingleLiveEvent() }

    val headerInfo: HeaderInfo get() = HeaderInfo(baseInfo)
    val galleryName: String get() = baseInfo.title
    val uploader: String get() = baseInfo.uploader
    val gid: Long get() = baseInfo.gid
    val token: String get() = baseInfo.token

    val isFavorited: Boolean
        get() = if (infoWrap.isSourceInitialized) infoWrap.sourceDetail.isFavorited else false
    val favoriteName: String?
        get() = if (infoWrap.isSourceInitialized) infoWrap.sourceDetail.favoriteName else null

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        baseInfo = bundle.getParcelable(DataKey.GALLERY_INFO)
            ?: throw IllegalStateException()
    }

    val itemTransitionName: String
        get() = baseInfo.itemTransitionName

    val galleryDetail: LiveData<PagingData<ImageSource>>
        get() = repository.galleryDetail(baseInfo.gid, baseInfo.token, mDetailPageIn, infoWrap)
            .cachedIn(viewModelScope)
            .asLiveData()

    fun submitRating(rating: Float) {
        viewModelScope.launch {
            when (val result = repository.rating(infoWrap.sourceDetail, rating)) {
                is RequestResult.SuccessResult -> {
                    var handle = false

                    if (infoWrap.partInfo.rating != result.data.avg.toFloat()) {
                        infoWrap.partInfo.rating = result.data.avg.toFloat()
                        handle = true
                    }

                    if (infoWrap.partInfo.ratingCount != result.data.count) {
                        infoWrap.partInfo.ratingCount = result.data.count
                        handle = true
                    }

                    toastData.postValue(string(R.string.hint_rate_successfully))
                    if (handle) rateNotify.postValue(NotifyItem.UpdateData(0))
                }
                is RequestResult.FailResult -> toastData.postValue(result.throwable.message)
            }
        }
    }

    fun submitFavorites(cat: Int) {
        viewModelScope.launch {
            when (repository.favorites(baseInfo.gid, baseInfo.token, cat)) {
                is RequestResult.SuccessResult -> {
                    val strRes =
                        if (cat < 0) R.string.hint_remove_favorite_success else R.string.hint_add_favorite_success

                    val name = GalleryFavorites.findName(cat)
                    RoomData.galleryDao.updateGalleryFavorites(baseInfo.gid, baseInfo.token, name)
                    infoWrap.sourceDetail.favoriteName = name

                    favNotify.postValue(name)
                    toastData.postValue(string(strRes))
                }
                is RequestResult.FailResult -> toastData.postValue(
                    string(
                        if (cat < 0) R.string.hint_remove_favorite_failure
                        else R.string.hint_add_favorite_failure
                    )
                )
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            RoomData.galleryDao.deleteGalleryInfo(baseInfo.gid, baseInfo.token)
        }
    }
}