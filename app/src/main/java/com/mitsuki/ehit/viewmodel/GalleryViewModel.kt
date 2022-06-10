package com.mitsuki.ehit.viewmodel

import android.os.Bundle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.loadprogress.addFeature
import com.mitsuki.ehit.crutch.extensions.postNext

import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.coil.CacheKey
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.GalleryPreview
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var mPreviewCache: GalleryPreview? = null

    private val mLoadUrl: MutableLiveData<String> = MutableLiveData()
    val loadUrl: LiveData<String> = mLoadUrl
    private val mState: MutableLiveData<LoadState> = MutableLiveData(LoadState())
    val state: LiveData<LoadState> = mState

    private var mLoadJob: Job? = null

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        index = bundle.getInt(DataKey.GALLERY_INDEX, 0)
        mId = bundle.getLong(DataKey.GALLERY_ID, -1)
        galleryToken =
            bundle.getString(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
    }

    fun obtainData() {
        mLoadJob?.cancel()
        mLoadJob = viewModelScope.launch {
            mState.postNext { it.copy(loading = true, error = null) }
            var error: String? = null
            with(repository.galleryPreview(mId, galleryToken, index)) {
                when (this) {
                    is RequestResult.Success -> {
                        mPreviewCache = data
                        mLoadUrl.postValue(data.imageUrl.addFeature(tag))
                    }
                    is RequestResult.Fail -> error = throwable.message
                }
            }
            mState.postNext { it.copy(loading = false, error = error) }
        }
    }

    fun retry() {
        mLoadJob?.cancel()
        mPreviewCache?.apply {
            mLoadJob = viewModelScope.launch {
                mState.postNext { it.copy(loading = true, error = null) }
                var error: String? = null
                with(repository.galleryPreview(reloadKey, mId, galleryToken, index)) {
                    when (this) {
                        is RequestResult.Success -> {
                            mPreviewCache = data
                            mLoadUrl.postValue(data.imageUrl.addFeature(tag))
                        }
                        is RequestResult.Fail -> error = throwable.message
                    }
                }
                mState.postNext { it.copy(loading = false, error = error) }
            }
        }

    }


    fun changeLoadingState(isVisible: Boolean) {
        mState.postNext { it.copy(loading = isVisible) }
    }

    data class LoadState(val loading: Boolean = false, val error: String? = null)
}