package com.mitsuki.ehit.model.download

import android.content.Intent
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.ui.download.service.DownloadService
import kotlinx.coroutines.runBlocking
import java.io.File

class DownloadThumbRunnable(
    private val gid: Long,
    private val token: String,
    private val repository: Repository
) :
    Runnable {
    override fun run() {
        runBlocking {
            when (val result = repository.downloadThumb(gid, token)) {
                is RequestResult.Success<File> -> {
                    sendFinishEvent(result.data)
                }
                is RequestResult.Fail<*> -> {
                    sendFinishEvent(null)
                }
            }
        }
    }


    private fun sendFinishEvent(file: File?) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            //TODO 添加数据回调
            action = DownloadService.DOWNLOAD_BROADCAST_THUMB
        })
    }
}