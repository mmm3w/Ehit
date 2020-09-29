package com.mitsuki.ehit.core.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.being.exception.DetailInitException
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.model.entity.obtainHeader
import com.mitsuki.ehit.core.model.repository.RemoteRepository
import com.mitsuki.ehit.core.model.repository.Repository

class GalleryDetailViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private lateinit var mBaseInfo: Gallery
    private val mDetailPageIn = PageIn()
    val detailWrap = GalleryDetailWrap()

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw DetailInitException()
        mBaseInfo =
            bundle.getParcelable(DataKey.GALLERY_INFO) ?: throw DetailInitException()
        detailWrap.headInfo = mBaseInfo.obtainHeader()
    }

    val itemTransitionName: String
        get() = mBaseInfo.itemTransitionName

    val galleryDetail: LiveData<PagingData<ImageSource>>
        get() = repository.galleryDetail(mBaseInfo.gid, mBaseInfo.token, mDetailPageIn, detailWrap)
            .cachedIn(viewModelScope)
            .asLiveData()

    fun galleryDetailPage(page: Int) {
        mDetailPageIn.jump(page)
    }

}