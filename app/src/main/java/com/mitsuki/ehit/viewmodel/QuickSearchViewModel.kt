package com.mitsuki.ehit.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.diff.Diff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class QuickSearchViewModel @Inject constructor(val searchDao: SearchDao) : ViewModel() {

    val data: NotifyQueueData<QuickSearch> = NotifyQueueData(Diff.QUICK_SEARCH)

    fun initData() {
        viewModelScope.launch {
            data.postUpdate(NotifyData.Refresh(searchDao.queryQuick()))
        }
    }

    fun remove(item: QuickSearch) {
        viewModelScope.launch {

            data.postUpdate(NotifyData.Remove(item))
            searchDao.deleteQuick(item.key, item.type)
        }
    }

    fun add(name: String, key: String, type: GalleryDataMeta.Type) {
        viewModelScope.launch {
            if (name.isNotEmpty() && key.isNotEmpty()) {
                val list = searchDao.queryQuick(key, type)
                if (list.isEmpty()) {
                    data.postUpdate(NotifyData.Insert(QuickSearch(type, name, key, 0)))
                    searchDao.saveQuick(name, key, type)
                }
            }
        }
    }

    fun move(fromPosition: Int, toPosition: Int) {
        viewModelScope.launch {
            data.postUpdate(NotifyData.Move(fromPosition, toPosition))
        }
    }

    fun resort() {
        viewModelScope.launch {
            val data = ArrayList<QuickSearch>().apply {
                for (i in 0 until data.count) {
                    add(data.item(i).apply { sort = i + 1 })
                }
            }
            searchDao.insertQuick(*data.toTypedArray())
        }
    }
}