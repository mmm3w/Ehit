package com.mitsuki.ehit.model.download

import android.content.Intent
import android.util.Log
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.ui.download.service.DownloadBroadcast
import com.mitsuki.ehit.ui.download.service.DownloadService
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque

class DownloadScheduler(private val repository: Repository) {
    private val mData: MutableMap<String, DownloadWork<DownloadNode>> = hashMapOf()
    private val mListData: MutableList<String> = arrayListOf()
    private val mDataLock = Mutex()


    private suspend fun innerAppend(tag: String, newNode: List<DownloadNode>) {
        mData[tag]?.apply { append(newNode) } ?: let {
            mData[tag] = DownloadWork(3, newNode, this::downloadPage).apply {
                mListData.add(tag)
            }
        }
    }

    suspend fun append(tag: String, newNode: List<DownloadNode>) {
        mDataLock.withLock { innerAppend(tag, newNode) }
    }

    suspend fun append(data: Map<String, List<DownloadNode>>) {
        mDataLock.withLock { data.forEach { entry -> innerAppend(entry.key, entry.value) } }
    }

    suspend fun cancelAll() {
        mDataLock.withLock {
            mData.forEach { entry ->
                entry.value.stop()
            }
            mData.clear()
            mListData.clear()
        }
    }

    suspend fun cancel(tag: String) {
        mDataLock.withLock {
            mData.remove(tag)?.apply {
                stop()
                mListData.remove(tag)
            }
        }
    }

    suspend fun singleWork(): Boolean {
        Log.d("Download", "single work start")
        return mDataLock.withLock {
            Log.d("Download", "single work get")
            val tag = mListData.firstOrNull()
            val data = mData[tag]
            if (tag == null || data == null) {
                null
            } else {
                tag to data
            }
        }?.let {
            it.second.exec()
            Log.d("Download", "single work finish")
            mDataLock.withLock {
                Log.d("Download", "single work remove")
                mData.remove(it.first)
                mListData.remove(it.first)
                mListData.isNotEmpty()
            }
        } ?: false
    }

    /**********************************************************************************************/

    private suspend fun downloadPage(node: DownloadNode, total: Int, over: Int) {


        DownloadBroadcast.sendStart(node.gid.toString(), total, over)

//            when (val result = repository.downloadPage(node.gid, node.token, node.page)) {
//                is RequestResult.Success<File> -> {
//                    sendFinishEvent(result.data)
//                }
//                is RequestResult.Fail<*> -> {
//                    sendFinishEvent(null)
//                }
//            }

        delay((1000L..5000L).random())
        DownloadBroadcast.sendFinish(node)
    }

    private fun downloadThumb() {
//        when (val result = repository.downloadThumb(gid, token)) {
//            is RequestResult.Success<File> -> {
//                sendFinishEvent(result.data)
//            }
//            is RequestResult.Fail<*> -> {
//                sendFinishEvent(null)
//            }
//        }

        DownloadBroadcast.sendThumbFinish()
    }
}