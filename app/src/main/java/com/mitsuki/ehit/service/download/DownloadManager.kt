package com.mitsuki.ehit.service.download

import android.util.Log
import android.webkit.MimeTypeMap
import com.mitsuki.ehit.const.DirManager
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.crutch.uils.BlockWork
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.entity.GalleryPreview
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.DownloadRepository
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject

class DownloadManager @Inject constructor(
    @RemoteRepository private val repository: Repository,
    private val downloadRepository: DownloadRepository,
    private val downloadDao: DownloadDao,
    private val shareData: ShareData
) {
    private val TAG = "EDownload"

    private val mData: MutableMap<String, BlockWork<DownloadNode>> = hashMapOf()
    private val mList: MutableList<String> = arrayListOf()
    private val mDataLock = Mutex()

    private val workLock = Mutex()
    private var workMark = false

    private var workJob: Job? = null


    fun postTask(message: DownloadMessage) {
        Log.d(TAG, "new task:$message")
        CoroutineScope(Dispatchers.Default).launch {
            mDataLock.withLock {
                val newNodes = downloadDao.updateDownloadList(message)
                innerAppend(key(message.gid, message.token), newNodes)
            }
            launch { downloadThumb(message.gid, message.token) }
            startWork()
        }
    }

    fun startTask(gid: Long, token: String) {
        Log.d(TAG, "restart $gid:$token")
        CoroutineScope(Dispatchers.Default).launch {
            mDataLock.withLock {
                val tag = key(gid, token)
                if (!mList.contains(tag)) {
                    val data = downloadDao.queryDownloadNodeWithNotState(gid, token, 1)
                    innerAppend(tag, data)
                }
            }
            launch { downloadThumb(gid, token) }
            startWork()
        }
    }

    fun startAll() {
        Log.d(TAG, "start all")
        CoroutineScope(Dispatchers.Default).launch {
            mDataLock.withLock {
                val infoList = downloadDao.queryALlDownloadInfo()
                infoList.forEach {
                    val tag = key(it.gid, it.token)
                    Log.d(TAG, "query $tag")
                    if (!mList.contains(tag)) {
                        val data = downloadDao.queryDownloadNodeWithNotState(it.gid, it.token, 1)
                        Log.d(TAG, "append $tag ${data.size}")
                        innerAppend(tag, data)
                    }
                }
            }
            Log.d(TAG, "$mList")
            Log.d(TAG, "$mData")

            //TODO thumb
            startWork()
        }
    }

    fun stopTask(gid: Long, token: String) {
        Log.d(TAG, "stop $gid:$token")
        CoroutineScope(Dispatchers.Default).launch {
            mDataLock.withLock {
                val tag = key(gid, token)
                mData.remove(tag)?.stop()
                mList.remove(tag)
            }
        }
    }

    fun stopAll() {
        CoroutineScope(Dispatchers.Default).launch {
            workJob?.cancel()
            mDataLock.withLock {
                mData.forEach { entry -> entry.value.stop() }
                mData.clear()
                mList.clear()
            }
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
                Log.d(TAG, "work loop step start")

                val result = mDataLock.withLock {
                    val tag = mList.firstOrNull()
                    Log.d(TAG, "single work get $tag")
                    val data = mData[tag]
                    if (tag == null || data == null) {
                        null
                    } else {
                        tag to data
                    }
                }?.let {
                    resolveKey(it.first).apply { DownloadBroadcast.sendStart(first, second) }
                    it.second.exec()
                    Log.d(TAG, "single work finish")
                    mDataLock.withLock {
                        Log.d(TAG, "single work remove")
                        mData.remove(it.first)
                        mList.remove(it.first)
                        mList.isNotEmpty()
                    }
                } ?: false

                if (result) {
                    Log.d(TAG, "next work")
                } else {
                    Log.d(TAG, "list is empty")
                    workLock.withLock { workMark = false }
                    DownloadBroadcast.sendFinish()
                }
            }
        }
    }

    private suspend fun innerAppend(tag: String, newNodes: List<DownloadNode>) {
        mData[tag]?.run { if (append(newNodes)) this else null } ?: let {
            mData[tag] = BlockWork(shareData.spDownloadThread, newNodes, this::downloadPage)
                .apply { mList.add(tag) }
        }
    }

    private fun key(g: Long, t: String) = "g:$g-$t"

    private fun resolveKey(key: String): Pair<Long, String> {
        val data = key.replace("g:", "").split("-")
        return data[0].toLong() to data[1]
    }

    /**********************************************************************************************/
    private suspend fun downloadPage(node: DownloadNode) {
        Log.d(TAG, "download page $node")
        when (val galleryInfo = repository.galleryPreview(node.gid, node.token, node.page)) {
            is RequestResult.Success<GalleryPreview> -> {

                val fileName = String.format("%09d", node.page) + "." +
                        MimeTypeMap.getFileExtensionFromUrl(galleryInfo.data.imageUrl)

                val result = downloadRepository.downloadImage(
                    galleryInfo.data.imageUrl,
                    DirManager.downloadCache(node.gid, node.token),
                    fileName
                )

                when (result) {
                    is RequestResult.Success<File> -> {
                        node.downloadState = 1
                        node.localPath = result.data.absolutePath
                        downloadDao.updateDownloadNode(node)
                        Log.d(TAG, "$node download success")
                    }
                    is RequestResult.Fail<*> -> {
                        node.downloadState = 2
                        downloadDao.updateDownloadNode(node)
                        Log.d(TAG, "$node download error: ${result.throwable}")
                    }
                }
            }
            is RequestResult.Fail<*> -> {
                Log.d(TAG, "$node get page info error: ${galleryInfo.throwable}")
                node.downloadState = 2
                downloadDao.updateDownloadNode(node)
            }
        }
    }

    private suspend fun downloadThumb(gid: Long, token: String) {
        downloadDao.queryDownloadInfo(gid, token)?.let { info ->
            if (info.localThumb.isNotEmpty()) return

            val fileName = "thumb_${gid}_$token.${MimeTypeMap.getFileExtensionFromUrl(info.thumb)}"
            val result = downloadRepository.downloadImage(
                info.thumb,
                DirManager.thumbCache(),
                fileName
            )

            when (result) {
                is RequestResult.Success<File> -> {
                    info.localThumb = result.data.absolutePath
                    downloadDao.updateDownloadInfo(info)
                }
                is RequestResult.Fail<*> -> {
                }
            }
        }
    }
}