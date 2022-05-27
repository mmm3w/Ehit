package com.mitsuki.ehit.service.download

import android.util.Log
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DownloadManager @Inject constructor(
    @RemoteRepository private val repository: Repository,
    private val downloadDao: DownloadDao,
    private val shareData: ShareData
) {
    private val tag = "EDownload"

    private val workLock = Mutex()
    private var workMark = false
    private var workJob: Job? = null

    private val downloadSchedule by lazy { DownloadScheduler(repository) }


    fun postTask(message: DownloadMessage) {
        Log.d(tag, "new task:$message")
        CoroutineScope(Dispatchers.Default).launch {
            val newNodes = downloadDao.updateDownloadList(message)
            //下载封面
            //投入任务
            //开始任务loop循环

            startWork()
        }
    }

    fun startAll() {

    }

    fun stopTask() {

    }

    fun stopAll() {
        CoroutineScope(Dispatchers.Default).launch {
            workJob?.cancel()
            downloadSchedule.cancelAll()
            workLock.withLock { workMark = false }
        }
    }

    fun destroy() {
        stopAll()
    }

    private suspend fun startWork() {
        workLock.withLock {
            if (workMark) return
            workMark = true
        }
        workJob?.cancel()
        workJob = CoroutineScope(Dispatchers.Default).launch {
            while (workMark) {
                Log.d(tag, "work loop step start")
                val result = downloadSchedule.singleWork()
                if (result) {
                    Log.d(tag, "next work")
                } else {
                    Log.d(tag, "list is empty")
                    workLock.withLock { workMark = false }
                    DownloadBroadcast.finish()
                }
            }
        }
    }
}