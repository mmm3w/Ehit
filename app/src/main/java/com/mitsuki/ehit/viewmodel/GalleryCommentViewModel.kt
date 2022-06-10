package com.mitsuki.ehit.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post

import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.extensions.postNext
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.repository.CommentRepository
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.ui.comment.adapter.LoadAllCommentAdapter
import com.mitsuki.ehit.ui.common.adapter.ListStatesAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryCommentViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    var commentRepository: CommentRepository
) :
    ViewModel(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private var mGalleryID: Long = -1L
    private lateinit var mGalleryToken: String
    private var mApiUID: Long = -1L
    private lateinit var mApiKey: String

    private var isInit = false

    private val _viewStates: MutableLiveData<ViewStates> by lazy { MutableLiveData(ViewStates()) }
    val viewStates: LiveData<ViewStates> get() = _viewStates

    private val _commentData: MutableStateFlow<List<Comment>> = MutableStateFlow(arrayListOf())
    val commentDataFlow: Flow<List<Comment>> get() = _commentData

    fun initData(intent: Intent?) {
        mGalleryID = intent?.getLongExtra(DataKey.GALLERY_ID, -1) ?: throw IllegalStateException()
        if (mGalleryID == -1L) throw  IllegalStateException()
        mGalleryToken =
            intent.getStringExtra(DataKey.GALLERY_TOKEN) ?: throw IllegalStateException()
        mApiUID = intent.getLongExtra(DataKey.GALLERY_API_UID, -1)
        if (mApiUID == -1L) throw  IllegalStateException()
        mApiKey = intent.getStringExtra(DataKey.GALLERY_API_KEY) ?: throw IllegalStateException()
    }

    fun loadComment(showAll: Boolean) {
        viewModelScope.launch {
            _viewStates.postNext {
                it.copy(
                    refreshState = !showAll && isInit, //初次加载之后全部使用下拉刷新头刷新
                    listState = if (isInit || showAll) it.listState else ListStatesAdapter.ListState.Refresh,
                    loadAllState = if (!isInit) LoadAllCommentAdapter.LoadState.Invisible else {
                        if (showAll) LoadAllCommentAdapter.LoadState.Loading else it.loadAllState
                    }
                )
            }

            when (val result =
                commentRepository.galleryComment(mGalleryID, mGalleryToken, showAll)) {
                is RequestResult.Success -> {
                    isInit = true

                    _viewStates.postNext {
                        it.copy(
                            refreshState = false,
                            listState =
                            if (result.data.isEmpty())
                                ListStatesAdapter.ListState.Message(string(R.string.text_no_comments))
                            else
                                ListStatesAdapter.ListState.None,
                            loadAllState = if (result.data.isEmpty() || showAll) LoadAllCommentAdapter.LoadState.Invisible else LoadAllCommentAdapter.LoadState.LoadMore
                        )
                    }
                    _commentData.value = result.data
                }
                is RequestResult.Fail -> {
                    _viewStates.postNext {
                        it.copy(
                            refreshState = false,
                            listState =
                            if (!isInit && !showAll)
                                ListStatesAdapter.ListState.Error(result.throwable)
                            else
                                ListStatesAdapter.ListState.None,
                            loadAllState = if (showAll) LoadAllCommentAdapter.LoadState.LoadMore else it.loadAllState
                        )
                    }
                    if (showAll) {
                        post("toast", result.throwable.message)
                    }
                }
            }
        }
    }

    fun voteComment(position: Int, comment: Comment, vote: Int) {
        viewModelScope.launch {
            when (val result =
                commentRepository.voteGalleryComment(
                    mApiKey,
                    mApiUID,
                    mGalleryID,
                    mGalleryToken,
                    comment.id,
                    vote
                )) {
                is RequestResult.Success -> {
                    //在这里刷新那条数据
                    post("vote", NotifyData.Change(position, comment.apply {
                        voteState = result.data.commentVote
                        score = result.data.toString()
                    }))
                }
                is RequestResult.Fail -> {
                    post("toast", result.throwable.message)
                }
            }
        }
    }

    data class ViewStates(
        val refreshState: Boolean = false,
        val listState: ListStatesAdapter.ListState = ListStatesAdapter.ListState.None,
        val loadAllState: LoadAllCommentAdapter.LoadState = LoadAllCommentAdapter.LoadState.Invisible
    )

}