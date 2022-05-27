package com.mitsuki.ehit.service.download

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.entity.DownloadMessage

object DownloadEvent {

    const val ACTION_START_ALL = "START_ALL"
    const val ACTION_DOWNLOAD = "DOWNLOAD"
    const val ACTION_RESTART = "RESTART"

    fun startDownload(context: Context, msg: DownloadMessage) {
        Intent(context, DownloadService::class.java).apply {
            action = ACTION_DOWNLOAD
            putExtra(DataKey.DOWNLOAD_TASK, msg)
            ContextCompat.startForegroundService(context, this)
        }
    }

    fun stopAllDownload() {
        DownloadBroadcast.sendStopAll()
    }

    fun stopDownload(gid: Long, token: String) {
        DownloadBroadcast.sendStop(gid, token)
    }

    fun restartDownload(context: Context, gid: Long, token: String) {
        Intent(context, DownloadService::class.java).apply {
            action = ACTION_RESTART
            putExtra(DataKey.GALLERY_ID, gid)
            putExtra(DataKey.GALLERY_TOKEN, token)
            ContextCompat.startForegroundService(context, this)
        }
    }

    fun startAllDownload(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, DownloadService::class.java).apply {
                action = ACTION_START_ALL
            })
    }
}