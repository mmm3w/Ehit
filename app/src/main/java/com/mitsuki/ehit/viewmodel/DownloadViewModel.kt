package com.mitsuki.ehit.viewmodel

import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.entity.DownloadListInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(val downloadDao: DownloadDao) : ViewModel() {

    suspend fun downloadList(): Flow<List<DownloadListInfo>> =
        withContext(Dispatchers.IO) { downloadDao.queryDownloadList() }






}