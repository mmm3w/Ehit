package com.mitsuki.ehit.viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.Log
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuickSearchViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    var type = GalleryListPageIn.Type.NORMAL
        private set
    var key = ""
        private set

    fun initData(bundle: Bundle?) {
        type = bundle?.getParcelable(DataKey.GALLERY_QUICK_TYPE) ?: GalleryListPageIn.Type.NORMAL
        key = bundle?.getString(DataKey.GALLERY_QUICK_KEY) ?: ""
    }

    suspend fun quickSearch(): List<QuickSearch> =
        withContext(Dispatchers.IO) { RoomData.searchDao.queryQuick() }


    fun saveSearch(name: String) {
        doInIO { if (name.isNotEmpty()) RoomData.searchDao.saveQuick(name, key, type) }
    }

    fun delSearch(key: String, type: GalleryListPageIn.Type) {
        doInIO { RoomData.searchDao.deleteQuick(key, type) }
    }

    fun swapQuickItem(data: List<QuickSearch>) {
        doInIO { RoomData.searchDao.insertQuick(*data.toTypedArray()) }
    }

    private fun doInIO(action: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { action() }
    }
}