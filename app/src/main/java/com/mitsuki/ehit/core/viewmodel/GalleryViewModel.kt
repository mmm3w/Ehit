package com.mitsuki.ehit.core.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.ehit.being.MemoryCache
import com.mitsuki.ehit.being.exception.DetailInitException
import com.mitsuki.ehit.being.extend.postNext
import com.mitsuki.ehit.being.imageloadprogress.addFeature
import com.mitsuki.ehit.being.network.RequestResult
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository
import kotlinx.coroutines.launch
import java.lang.Exception

class GalleryViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    var index: Int = 0
    private var mId: Long = -1
    lateinit var galleryToken: String
    private var mPToken: String = ""

    private val mData: MutableLiveData<String> = MutableLiveData()
    val data: LiveData<String> = mData
    private val mState: MutableLiveData<ViewState> = MutableLiveData(ViewState())
    val state: LiveData<ViewState> = mState

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw DetailInitException()
        index = bundle.getInt(DataKey.GALLERY_INDEX, 0)
        mId = bundle.getLong(DataKey.GALLERY_ID, -1)
        bundle.getString(DataKey.IMAGE_TOKEN)?.apply { mPToken = this }
        galleryToken =
            bundle.getString(DataKey.GALLERY_TOKEN) ?: throw Exception("Missing gallery token")
    }

    fun obtainData() {
        viewModelScope.launch {
            mState.postNext { it.copy(loading = true) }
            if (mPToken.isEmpty()){
                val detailIndex =
                    if (MemoryCache.detailPageSize > 0) index / MemoryCache.detailPageSize else 0
                with(repository.galleryDetailWithPToken(mId, galleryToken, detailIndex)) {
                    when (this) {
                        is RequestResult.SuccessResult -> mPToken = data
                        is RequestResult.FailResult -> {
                            mState.postNext { it.copy(loading = false, error = throwable.message) }
                            return@launch
                        }
                    }
                }
            }
            var error: String? = null
            with(repository.galleryPreview(mId, mPToken, index)) {
                when (this) {
                    is RequestResult.SuccessResult -> mData.postValue(data.imageUrl.addFeature(tag()))
                    is RequestResult.FailResult -> error = throwable.message
                }
            }
            mState.postNext { it.copy(loading = false, error = error) }
        }
    }

    fun tag() = "$mId-$index"

    data class ViewState(val loading: Boolean = false, val error: String? = null)
}