package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.model.entity.SearchKey
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SearchViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    @Suppress("PrivatePropertyName")
    private val HISTORY_COUNT = 3
    var code: Int = -1
    var tempKey:SearchKey? = null

    fun initData(bundle: Bundle?) {
        if (bundle == null) throw RuntimeException("no data")
        code = bundle.getInt(DataKey.GALLERY_FRAGMENT_CODE)
        tempKey = bundle.getParcelable(DataKey.GALLERY_SEARCH_KEY)
    }

    suspend fun searchHistory(): Flow<List<SearchHistory>> =
        withContext(Dispatchers.IO) { RoomData.searchDao.queryHistory(HISTORY_COUNT) }

    suspend fun quickSearch(): Flow<List<QuickSearch>> =
        withContext(Dispatchers.IO) { RoomData.searchDao.queryQuick() }

    suspend fun saveSearch(text: String) = withContext(Dispatchers.IO) {
        if (text.isNotEmpty())
            RoomData.searchDao.insertHistory(SearchHistory(text, System.currentTimeMillis()))
    }


}