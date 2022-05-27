package com.mitsuki.ehit.service.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.entity.db.DownloadNode

internal object DownloadBroadcast {


    const val DOWNLOAD_FINISH = "DOWNLOAD_FINISH"


    const val DOWNLOAD_BROADCAST_PAGE = "DOWNLOAD_PAGE_ACTION"
    const val DOWNLOAD_BROADCAST_STOP = "DOWNLOAD_BROADCAST_STOP"
    const val DOWNLOAD_BROADCAST_STOP_ALL = "DOWNLOAD_BROADCAST_STOP_ALL"


    const val FINISH_NODE = "FINISH_NODE"
    const val TASK_NAME = "TASK_NAME"
    const val TASK_TOTAL = "TASK_TOTAL"
    const val TASK_OVER = "TASK_OVER"

    fun registerReceiver(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter().apply {
                addAction(DOWNLOAD_FINISH)
                addAction(DOWNLOAD_BROADCAST_PAGE)
                addAction(DOWNLOAD_BROADCAST_STOP)
                addAction(DOWNLOAD_BROADCAST_STOP_ALL)
            })
    }

    fun unregisterReceiver(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    fun finish() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply { action = DOWNLOAD_FINISH })
    }

    fun sendFinish(
        node: DownloadNode,
        name: String,
        path: String?,
        state: Int,
        total: Int,
        over: Int
    ) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            putExtra(TASK_NAME, name)
            putExtra(TASK_TOTAL, total)
            putExtra(TASK_OVER, over)
            putExtra(
                FINISH_NODE,
                DownloadNode(
                    node.gid,
                    node.token,
                    node.page,
                    state,
                    path ?: ""
                )
            )
            action = DOWNLOAD_BROADCAST_PAGE
        })
    }


    fun sendStop(gid: Long, token: String) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_BROADCAST_STOP
            putExtra(DataKey.DOWNLOAD_TARGET, gid to token)
        })
    }

    fun sendStopAll() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_BROADCAST_STOP_ALL
        })
    }
}