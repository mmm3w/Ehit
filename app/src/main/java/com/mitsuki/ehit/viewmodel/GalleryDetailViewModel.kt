package com.mitsuki.ehit.viewmodel

import android.os.Bundle

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryDetailViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    val galleryDao: GalleryDao
) :
    ViewModel(), EventEmitter {

    var gid: Long = -1L
        private set
    var token: String = ""
        private set

    val itemTransitionName: String
        get() = "gallery:$gid$token"

    override val eventEmitter: Emitter = Emitter()

    val infoWrap = GalleryDetailWrap()
    private val mDetailPageIn = GeneralPageIn()

    val title: String get() = infoWrap.headerInfo.title
    val galleryName: String get() = infoWrap.headerInfo.title
    val uploader: String get() = infoWrap.headerInfo.uploader

    val tempDownloadMessage: DownloadMessage
        get() = DownloadMessage(gid, token, 1, infoWrap.page, infoWrap.thumb, infoWrap.title)

    val isFavorited: Boolean
        get() = if (infoWrap.isSourceInitialized) infoWrap.sourceDetail.isFavorited else false

    val favoriteName: String?
        get() = if (infoWrap.isSourceInitialized) infoWrap.sourceDetail.favoriteName else null

    val galleryDetail: LiveData<PagingData<ImageSource>>
        get() = repository.galleryDetail(gid, token, mDetailPageIn, infoWrap)
            .cachedIn(viewModelScope)
            .asLiveData()

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        //通过url打开仅有token和gid，通过点击Item打开拥有全部数据
        val info: Gallery =
            bundle.getParcelable(DataKey.GALLERY_INFO) ?: throw IllegalStateException()

        this.gid = info.gid
        this.token = info.token
        if (gid == -1L || token.isEmpty()) throw IllegalStateException()

        infoWrap.headerInfo = GalleryDetailWrap.HeaderInfo(info)
    }

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
                    post("toast", string(R.string.hint_rate_successfully))
                    if (handle) post("rate", "")
                }
                is RequestResult.FailResult -> post("toast", result.throwable.message)
            }
        }
    }

    fun submitFavorites(cat: Int) {
        viewModelScope.launch {
            when (repository.favorites(gid, token, cat)) {
                is RequestResult.SuccessResult -> {
                    val strRes =
                        if (cat < 0) R.string.hint_remove_favorite_success else R.string.hint_add_favorite_success

                    val name = GalleryFavorites.findName(cat)
                    galleryDao.updateGalleryFavorites(gid, token, name)
                    infoWrap.sourceDetail.favoriteName = name

                    post("fav", 0)
                    post("toast", string(strRes))
                }
                is RequestResult.FailResult -> post(
                    "toast",
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
            galleryDao.deleteGalleryInfo(gid, token)
        }
    }


}