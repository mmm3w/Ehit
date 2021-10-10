package com.mitsuki.ehit.viewmodel

import android.content.Intent
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GalleryCommentViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private val _commentData: MutableStateFlow<List<Comment>> = MutableStateFlow(arrayListOf())
    val commentDataFlow: Flow<List<Comment>> get() = _commentData

    private val _loadState: MutableStateFlow<LoadState> =
        MutableStateFlow(LoadState.NotLoading(true))
    val loadStateFlow: Flow<LoadState> get() = _loadState

    private var mGalleryID: Long = -1L
    private lateinit var mGalleryToken: String

    fun initData(intent: Intent?) {
        mGalleryID = intent?.getLongExtra(DataKey.GALLERY_ID, -1) ?: throw IllegalStateException()
        if (mGalleryID == -1L) throw  IllegalStateException()
        mGalleryToken =
            intent.getStringExtra(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
    }


    fun loadComment(isShowAll: Boolean) {
        viewModelScope.launch {
            _loadState.value = LoadState.Loading
            when (val result =
                repository.galleryComment(mGalleryID, mGalleryToken, isShowAll)) {
                is RequestResult.SuccessResult -> {
                    _loadState.value = LoadState.NotLoading(false)
                    _commentData.value = result.data
                }
                is RequestResult.FailResult -> {
                    _loadState.value = LoadState.Error(result.throwable)
                    post("toast", result.throwable.message)
                }
            }
        }
    }

    fun sendComment(text: String) {
        viewModelScope.launch {
            if (text.isEmpty()) {
                return@launch
            }

            when (val result =
                repository.sendGalleryComment(mGalleryID, mGalleryToken, text)) {
                is RequestResult.SuccessResult -> {
                    Log.d("asdf", "成功")
                }
                is RequestResult.FailResult -> {
//                    Log.d("asdf", "${result.throwable.message}")
                    post("toast", result.throwable.message)
                }
            }
        }
    }


}