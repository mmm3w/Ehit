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
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.GalleryDetailPageIn
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

class GalleryDetailViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private val eventSubject: PublishSubject<Event> = PublishSubject.create()
    val event get() = eventSubject.hideWithMainThread()

    lateinit var baseInfo: Gallery
    private val mDetailPageIn = GalleryDetailPageIn()
    val infoWrap = GalleryDetailWrap()

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        baseInfo =
            bundle.getParcelable(DataKey.GALLERY_INFO) ?: throw IllegalStateException()
        val cacheKey = bundle.getParcelable<MemoryCache.Key>(DataKey.IMAGE_CACHE_KEY)
        infoWrap.headInfo = baseInfo.obtainHeader(cacheKey)
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

                    postEvent(
                        message = AppHolder.string(R.string.hint_rate_successfully),
                        notifyItem = if (handle) NotifyItem.UpdateData(0) else null
                    )
                }
                is RequestResult.FailResult -> postEvent(message = result.throwable.message)
            }
        }
    }


    data class Event(val message: String?, val rateNotifyItem: NotifyItem?)

    private fun postEvent(message: String? = null, notifyItem: NotifyItem? = null) {
        eventSubject.onNext(Event(message, notifyItem))
    }
}