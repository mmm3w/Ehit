package com.mitsuki.ehit.model.download

import android.content.Intent
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.ui.download.service.DownloadService
import kotlinx.coroutines.runBlocking
import java.io.File

class DownloadRunnable(private val node: DownloadNode, private val repository: Repository) :
    Runnable {
    override fun run() {
        runBlocking {
            when (val result = repository.downloadPage(node.gid, node.token, node.page)) {
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
            putExtra(
                DownloadService.FINISH_NODE,
                DownloadNode(
                    node.gid,
                    node.token,
                    node.page,
                    if (file != null) 1 else 2,
                    file?.absolutePath ?: ""
                )
            )
            action = DownloadService.DOWNLOAD_BROADCAST_PAGE
        })
    }
}