package com.mitsuki.ehit.viewmodel

import android.content.Intent

import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.model.entity.GalleryDataType
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @RemoteRepository var repository: Repository,
    val searchDao: SearchDao
) :
    ViewModel() {

    @Suppress("PrivatePropertyName")
    private val HISTORY_COUNT = 20
    lateinit var dataType: GalleryDataType

//    val galleryDataKey: com.mitsuki.ehit.model.entity.GalleryDataKey get() = dataType.searchKeyProvider()

//    fun buildNewSource(galleryDataKey: com.mitsuki.ehit.model.entity.GalleryDataKey): GalleryDataType {
////        return when (dataType) {
////            is GalleryDataType.Normal,
////            is GalleryDataType.Uploader,
////            is GalleryDataType.Tag -> GalleryDataType.Normal(galleryDataKey)
////            is GalleryDataType.Subscription -> GalleryDataType.Subscription(galleryDataKey)
////            else -> throw IllegalStateException() //TODO 有额外的一个类型需要处理
////        }
//    }

    fun initData(intent: Intent?) {
//        dataType = intent?.getParcelableExtra(DataKey.GALLERY_PAGE_SOURCE)
//            ?: GalleryDataType.DEFAULT_NORMAL
    }

    suspend fun searchHistory(): Flow<List<SearchHistory>> =
        withContext(Dispatchers.IO) { searchDao.queryHistory(HISTORY_COUNT) }

    suspend fun saveSearch(text: String) = withContext(Dispatchers.IO) {
        if (text.isNotEmpty()) searchDao.insertHistory(SearchHistory(text))
    }

    suspend fun delSearch(data: SearchHistory) = withContext(Dispatchers.IO) {
        searchDao.deleteHistory(data)
    }
}