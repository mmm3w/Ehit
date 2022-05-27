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
    const val DOWNLOAD_START = "DOWNLOAD_START"
    const val DOWNLOAD_FINISH = "DOWNLOAD_FINISH"
    const val DOWNLOAD_STOP_ALL = "DOWNLOAD_STOP_ALL"
    const val DOWNLOAD_STOP = "DOWNLOAD_STOP"




//    const val FINISH_NODE = "FINISH_NODE"
//    const val TASK_NAME = "TASK_NAME"
//    const val TASK_TOTAL = "TASK_TOTAL"
//    const val TASK_OVER = "TASK_OVER"

    fun registerReceiver(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter().apply {
                addAction(DOWNLOAD_START)
                addAction(DOWNLOAD_FINISH)
                addAction(DOWNLOAD_STOP)
                addAction(DOWNLOAD_STOP_ALL)
            })
    }

    fun unregisterReceiver(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    fun sendStart(gid: Long, token: String) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_START
            putExtra(DataKey.GALLERY_ID, gid)
            putExtra(DataKey.GALLERY_TOKEN, token)
        })
    }

    fun sendFinish() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply { action = DOWNLOAD_FINISH })
    }

    fun sendStop(gid: Long, token: String) {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_STOP
            putExtra(DataKey.GALLERY_ID, gid)
            putExtra(DataKey.GALLERY_TOKEN, token)
        })
    }

    fun sendStopAll() {
        AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
            action = DOWNLOAD_STOP_ALL
        })
    }
}