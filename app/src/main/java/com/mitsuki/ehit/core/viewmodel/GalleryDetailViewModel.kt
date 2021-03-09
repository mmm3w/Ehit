package com.mitsuki.ehit.core.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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

}