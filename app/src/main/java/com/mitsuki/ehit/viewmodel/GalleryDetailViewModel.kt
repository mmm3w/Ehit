package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData

import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.extensions.postNext
import com.mitsuki.ehit.crutch.extensions.setNext
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.repository.PagingRepository
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.IllegalArgumentException

@HiltViewModel
class GalleryDetailViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    var pagingData: PagingRepository,
    val galleryDao: GalleryDao
) : ViewModel(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    var gid: Long = -1L
        private set
    var token: String = ""
        private set

    private val mDetailPageIn = GeneralPageIn()

    val title: String get() = mCachedInfo?.title ?: throw IllegalArgumentException()
    val galleryName: String
        get() {
            return mCachedInfo?.title?.replace(Regex("\\[.*?]|\\(.*?\\)"), "")
                ?: throw IllegalArgumentException()
        }
    val uploader: String get() = mCachedInfo?.uploader ?: throw IllegalArgumentException()
    val favoriteName: String? get() = mCachedInfo?.favoriteName
    val apiKey get() = mCachedInfo?.apiKey ?: throw IllegalArgumentException()
    val apiUID get() = mCachedInfo?.apiUID ?: throw IllegalArgumentException()
    val rating get() = mCachedInfo?.rating ?: throw IllegalArgumentException()
    val page get() = mCachedInfo?.pages ?: throw IllegalArgumentException()
    val thumb get() = mCachedInfo?.detailThumb ?: throw IllegalArgumentException()

    val itemTransitionName: String get() = "gallery:$gid$token"

    val detailImage: Flow<PagingData<ImageSource>>
        get() = pagingData.detailImage(gid, token, mDetailPageIn)
            .cachedIn(viewModelScope)

    var viewTranslationY: Float = 0F


    /**********************************************************************************************/
    private var mCachedInfo: GalleryDetail? = null

    private val _infoStates: MutableLiveData<InfoStates> by lazy { MutableLiveData(InfoStates()) }
    val infoStates: LiveData<InfoStates> get() = _infoStates

    val myTags: NotifyQueueData<TagGroup> by lazy { NotifyQueueData(Diff.GALLERY_DETAIL_TAG) }
    val myComments: NotifyQueueData<Comment> by lazy { NotifyQueueData(Diff.GALLERY_COMMENT) }

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw IllegalStateException()
        //通过url打开仅有token和gid，通过点击Item打开拥有全部数据
        val info: Gallery =
            bundle.getParcelable(DataKey.GALLERY_INFO) ?: run {
                val gid = bundle.getString(DataKey.GALLERY_ID)?.toLongOrNull() ?: -1
                val token = bundle.getString(DataKey.GALLERY_TOKEN) ?: ""
                Gallery(gid, token)
            }

        this.gid = info.gid
        this.token = info.token
        if (gid == -1L || token.isEmpty()) throw IllegalStateException("error detail info. gid:$gid token:$token")

        _infoStates.setNext { it.copy(header = DetailHeader(info)) }
    }

    fun loadInfo() {
        viewModelScope.launch {
            if (mCachedInfo == null) {
                _infoStates.postNext {
                    it.copy(loadState = LoadState.Loading)
                }
            }
            post("loading", true)
            when (val result = repository.galleryDetailInfo(gid, token)) {
                is RequestResult.Success<GalleryDetail> -> {

                    _infoStates.postNext {
                        it.copy(
                            header = result.data.obtainHeaderInfo(),
                            loadState = LoadState.NotLoading(true),
                            part = result.data.obtainOperating(),
                            commentState = result.data.obtainCommentState(),
                            favorite = result.data.isFavorited
                        )
                    }
                    //部分数据依赖ViewModel中缓存的数据更新队列进行更新
                    myTags.postUpdate(NotifyData.Refresh(result.data.tagGroup.toList()))
                    result.data.comments.toList().also {
                        myComments.postUpdate(
                            NotifyData.Refresh(it.subList(0, it.size.coerceAtMost(3)))
                        )
                    }

                    mCachedInfo = result.data

                    post("newData", true)
                    post("datapick", 0)
                }
                is RequestResult.Fail -> {
                    if (mCachedInfo == null) {
                        _infoStates.postNext { it.copy(loadState = LoadState.Error(result.throwable)) }
                    } else {
                        /* toast */
                        post("toast", result.throwable.message)
                    }
                }
            }
            post("loading", false)
        }
    }

    fun submitRating(r: Float) {
        viewModelScope.launch {
            when (val result = repository.rating(gid, token, apiUID, apiKey, r)) {
                is RequestResult.Success -> {
                    post("toast", string(R.string.hint_rate_successfully))
                    _infoStates.postNext {
                        it.copy(
                            part = DetailPart(
                                result.data.avg.toFloat(),
                                result.data.count,
                                page
                            )
                        )
                    }
                }
                is RequestResult.Fail -> post("toast", result.throwable.message)
            }
        }
    }

    fun submitFavorites(cat: Int) {
        viewModelScope.launch {
            when (repository.favorites(gid, token, cat)) {
                is RequestResult.Success -> {
                    val name = GalleryFavorites.findName(cat)
                    galleryDao.updateGalleryFavorites(gid, token, name)
                    mCachedInfo?.favoriteName = name

                    _infoStates.postNext { it.copy(favorite = cat >= 0) }
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
        return DownloadMessage(gid, token, start - 1, end - 1, thumb, title)
    }

    data class InfoStates(
        val header: DetailHeader = DetailHeader.DEFAULT,
        val loadState: LoadState = LoadState.NotLoading(true),
        val part: DetailPart? = null,
        val commentState: CommentState? = null,
        val favorite: Boolean? = null
    )
}