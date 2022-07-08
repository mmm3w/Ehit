package com.mitsuki.ehit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.GalleryInfoCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    val galleryDao: GalleryDao,
) : ViewModel() {
    val data: NotifyQueueData<GalleryInfoCache> = NotifyQueueData(Diff.GALLERY_CACHE)

    fun load() {
        viewModelScope.launch {
            val result = galleryDao.queryGalleryHistory(50)
            data.postUpdate(NotifyData.Refresh(result))
        }
    }
}