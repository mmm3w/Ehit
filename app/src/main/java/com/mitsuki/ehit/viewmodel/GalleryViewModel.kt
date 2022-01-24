package com.mitsuki.ehit.viewmodel

import android.os.Bundle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.loadprogress.addFeature
import com.mitsuki.ehit.crutch.extensions.postNext
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.coil.CacheKey
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    var index: Int = 0
    private var mId: Long = -1
    lateinit var galleryToken: String

    val tag get() = CacheKey.previewKey(mId, galleryToken, index + 1)

    val largeCacheTag get() = CacheKey.largeTempKey(tag)

    private val mData: MutableLiveData<String> = MutableLiveData()
    val data: LiveData<String> = mData
    private val mState: MutableLiveData<ViewState> = MutableLiveData(ViewState())
    val state: LiveData<ViewState> = mState

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        index = bundle.getInt(DataKey.GALLERY_INDEX, 0)
        mId = bundle.getLong(DataKey.GALLERY_ID, -1)
        galleryToken =
            bundle.getString(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
    }

    fun obtainData() {
        viewModelScope.launch {
            mState.postNext { it.copy(loading = true) }

            val pToken: String

            with(repository.galleryDetailWithPToken(mId, galleryToken, index)) {
                when (this) {
                    is RequestResult.SuccessResult -> pToken = data
                    is RequestResult.FailResult -> {
                        mState.postNext { it.copy(loading = false, error = throwable.message) }
                        return@launch
                    }
                }
            }

            var error: String? = null
            with(repository.galleryPreview(mId, galleryToken, pToken, index)) {
                when (this) {
                    is RequestResult.SuccessResult -> mData.postValue(data.imageUrl.addFeature(tag))
                    is RequestResult.FailResult -> error = throwable.message
                }
            }
            mState.postNext { it.copy(loading = error == null, error = error) }
        }
    }

    fun changeLoadingState(isVisible: Boolean) {
        mState.postNext { it.copy(loading = isVisible) }
    }

    data class ViewState(val loading: Boolean = false, val error: String? = null)
}