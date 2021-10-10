package com.mitsuki.ehit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.page.GalleryPageSource
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.*

class QuickSearchViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    suspend fun quickSearch(): List<QuickSearch> =
        withContext(Dispatchers.IO) { RoomData.searchDao.queryQuick() }

    suspend fun isQuickSave(key: String, type: GalleryPageSource.Type): Boolean =
        withContext(Dispatchers.IO) {
            val list = RoomData.searchDao.queryQuick(key, type)
            list.isNotEmpty()
        }

    suspend fun saveSearch(name: String, key: String, type: GalleryPageSource.Type) =
        withContext(Dispatchers.IO) {
            if (name.isNotEmpty() && key.isNotEmpty())
                RoomData.searchDao.saveQuick(name, key, type)
        }

    suspend fun delSearch(key: String, type: GalleryPageSource.Type) = withContext(Dispatchers.IO) {
        RoomData.searchDao.deleteQuick(key, type)
    }

    suspend fun swapQuickItem(data: List<QuickSearch>)=withContext(Dispatchers.IO)  {
        RoomData.searchDao.insertQuick(*data.toTypedArray())
    }
}