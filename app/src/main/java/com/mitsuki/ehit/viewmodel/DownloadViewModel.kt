package com.mitsuki.ehit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsuki.ehit.const.DirManager
import com.mitsuki.ehit.crutch.extensions.clear
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.entity.DownloadListInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(var downloadDao: DownloadDao) : ViewModel() {

    suspend fun downloadList(): Flow<List<DownloadListInfo>> =
        withContext(Dispatchers.IO) {
            downloadDao.queryDownloadList()
        }


    fun deleteDownload(gid: Long, token: String) {
        viewModelScope.launch(Dispatchers.Default) {
            downloadDao.deleteDownloadInfo(gid, token)
            DirManager.downloadCache(gid, token).clear()
        }
    }


}