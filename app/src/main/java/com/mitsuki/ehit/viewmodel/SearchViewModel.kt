package com.mitsuki.ehit.viewmodel

import android.content.Intent

import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.entity.GalleryDataKey
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
    lateinit var searchKey: GalleryDataKey
        private set

    fun initData(intent: Intent?) {
        searchKey = intent?.getParcelableExtra(DataKey.GALLERY_SEARCH_KEY) ?: GalleryDataKey.DEFAULT
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