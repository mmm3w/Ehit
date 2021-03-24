package com.mitsuki.ehit.core.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.armory.adapter.NotifyItem
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.AppHolder
import com.mitsuki.ehit.being.extend.hideWithMainThread
import com.mitsuki.ehit.being.network.RequestResult
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.model.entity.obtainHeader
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

class GalleryDetailViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private val eventSubject: PublishSubject<Event> = PublishSubject.create()
    val event get() = eventSubject.hideWithMainThread()

    lateinit var baseInfo: Gallery
    private val mDetailPageIn = PageIn()
    val detailWrap = GalleryDetailWrap()

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        baseInfo =
            bundle.getParcelable(DataKey.GALLERY_INFO) ?: throw IllegalStateException()
        detailWrap.headInfo = baseInfo.obtainHeader()
    }

    val itemTransitionName: String
        get() = baseInfo.itemTransitionName

    val galleryDetail: LiveData<PagingData<ImageSource>>
        get() = repository.galleryDetail(baseInfo.gid, baseInfo.token, mDetailPageIn, detailWrap)
            .cachedIn(viewModelScope)
            .asLiveData()

    fun galleryDetailPage(page: Int) {
        mDetailPageIn.targetPage = page
    }

    fun submitRating(rating: Float) {
        viewModelScope.launch {
            when (val result = repository.rating(detailWrap.sourceDetail, rating)) {
                is RequestResult.SuccessResult -> {
                    var handle = false

                    if (detailWrap.partInfo.rating != result.data.avg.toFloat()) {
                        detailWrap.partInfo.rating = result.data.avg.toFloat()
                        handle = true
                    }

                    if (detailWrap.partInfo.ratingCount != result.data.count) {
                        detailWrap.partInfo.ratingCount = result.data.count
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