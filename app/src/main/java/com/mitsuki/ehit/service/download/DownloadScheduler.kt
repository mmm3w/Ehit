package com.mitsuki.ehit.service.download

import android.util.Log
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.BlockWork
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class DownloadScheduler(private val repository: Repository) {
    private val mData: MutableMap<String, BlockWork<DownloadNode>> = hashMapOf()
    private val mListData: MutableList<String> = arrayListOf()
    private val mDataLock = Mutex()

    private suspend fun innerAppend(tag: String, newNode: List<DownloadNode>, threadCount: Int) {
        mData[tag]?.apply { append(newNode) } ?: let {
            mData[tag] = BlockWork(threadCount, newNode, this::downloadPage).apply {
                mListData.add(tag)
            }
        }
    }

    suspend fun append(tag: String, newNode: List<DownloadNode>, threadCount: Int) {
        mDataLock.withLock { innerAppend(tag, newNode, threadCount) }
    }

    suspend fun append(data: Map<String, List<DownloadNode>>, threadCount: Int) {
        mDataLock.withLock {
            data.forEach { entry ->
                innerAppend(
                    entry.key,
                    entry.value,
                    threadCount
                )
            }
        }
    }

    suspend fun cancelAll() {
        mDataLock.withLock {
            mData.forEach { entry -> entry.value.stop() }
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

    suspend fun thumb(gid: Long, token: String) {
        repository.downloadThumb(gid, token)
    }

    /**********************************************************************************************/

    private suspend fun downloadPage(
        node: DownloadNode,
        total: Int,
        data: BlockWork<DownloadNode>
    ) {
        val name = repository.queryGalleryName(node.gid, node.token)
        when (val result = repository.downloadPage(node.gid, node.token, node.page)) {
            is RequestResult.Success<File> -> {
                DownloadBroadcast.sendFinish(
                    node,
                    name,
                    result.data.absolutePath,
                    1,
                    total,
                    data.down
                )
            }
            is RequestResult.Fail<*> -> {
                DownloadBroadcast.sendFinish(node, node.gid.toString(), null, 2, total, data.down)
            }
        }
    }
}