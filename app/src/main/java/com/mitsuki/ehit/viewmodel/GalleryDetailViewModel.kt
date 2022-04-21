package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn

import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryDetailViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    val galleryDao: GalleryDao
) : ViewModel(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private lateinit var galleryDetail: GalleryDetail
    var gid: Long = -1L
        private set
    var token: String = ""
        private set

    val headerInfo: MutableLiveData<HeaderInfo> by lazy { MutableLiveData() }
    val detailInfo: MutableLiveData<GalleryDetail> by lazy { MutableLiveData() }
    val initLoading: MutableLiveData<Pair<Boolean, Throwable?>> by lazy { MutableLiveData((true to null)) }
    val favorite: MutableLiveData<Boolean?> by lazy { MutableLiveData(null) }
    val loadSign: MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    val detailPart: MutableLiveData<DetailPart?> by lazy { MutableLiveData(null) }

    private val mDetailPageIn = GeneralPageIn()

    val title: String get() = galleryDetail.title
    val galleryName: String get() = galleryDetail.title
    val uploader: String get() = galleryDetail.uploader
    val favoriteName: String? get() = galleryDetail.favoriteName
    val apiKey get() = galleryDetail.apiKey
    val apiUID get() = galleryDetail.apiUID
    val rating get() = galleryDetail.rating
    val page get() = galleryDetail.pages
    val thumb get() = galleryDetail.detailThumb

    val itemTransitionName: String get() = "gallery:$gid$token"

    val detailImage: LiveData<PagingData<ImageSource>>
        get() = repository.detailImage(gid, token, mDetailPageIn)
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

        headerInfo.postValue(HeaderInfo(info))
    }

    fun loadInfo() {
        viewModelScope.launch {
            when (val result = repository.galleryDetailInfo(gid, token)) {
                is RequestResult.Success<GalleryDetail> -> {
                    galleryDetail = result.data
                    headerInfo.postValue(result.data.obtainHeaderInfo())
                    initLoading.postValue(false to null)
                    detailInfo.postValue(result.data)
                    favorite.postValue(result.data.isFavorited)
                    loadSign.postValue(true)
                    detailPart.postValue(result.data.obtainOperating())
                }
                is RequestResult.Fail -> {
                    initLoading.postValue(true to result.throwable)
                }
            }
        }
    }

    fun submitRating(r: Float) {
        viewModelScope.launch {
            when (val result = repository.rating(gid, token, apiUID, apiKey, r)) {
                is RequestResult.Success -> {
                    post("toast", string(R.string.hint_rate_successfully))
                    detailPart.postValue(
                        DetailPart(result.data.avg.toFloat(), result.data.count, page)
                    )
                }
                is RequestResult.Fail -> post("toast", result.throwable.message)
            }
        }
    }

    fun submitFavorites(cat: Int) {
        viewModelScope.launch {
            when (repository.favorites(gid, token, cat)) {
                is RequestResult.Success -> {
                    val strRes =
                        if (cat < 0) R.string.hint_remove_favorite_success else R.string.hint_add_favorite_success

                    val name = GalleryFavorites.findName(cat)
                    galleryDao.updateGalleryFavorites(gid, token, name)

                    favorite.postValue(cat >= 0)
                    post("toast", string(strRes))
                }
                is RequestResult.Fail -> post(
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

    fun obtainDownloadMessage(start: Int, end: Int): DownloadMessage {
        return DownloadMessage(gid, token, start, end, thumb, title)
    }


}