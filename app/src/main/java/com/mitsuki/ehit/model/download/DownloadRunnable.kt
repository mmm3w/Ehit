package com.mitsuki.ehit.model.download

import android.content.Intent
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.ui.download.service.DownloadService
import kotlinx.coroutines.runBlocking

class DownloadRunnable(private val node: DownloadNode) : Runnable {
    override fun run() {
        runBlocking {
            Thread.sleep(3000)
            sendFinishEvent()
        }
    }


    private fun sendFinishEvent() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            putExtra(
                DownloadService.FINISH_NODE,
                DownloadNode(node.gid, node.token, node.page, 1)
            )
            action = DownloadService.BROADCAST_ACTION
        })
    }
}