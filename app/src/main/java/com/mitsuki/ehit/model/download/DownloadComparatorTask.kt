package com.mitsuki.ehit.model.download

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.entity.DownloadTask
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.ui.download.service.DownloadService
import com.mitsuki.ehit.ui.download.service.DownloadService.Companion.FINISH_NODE
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

class DownloadComparatorTask(repository: Repository, task: DownloadTask) :
    FutureTask<Any>(DownloadRunnable(task, repository), null),
    Comparable<DownloadComparatorTask> {

    private val priority: Long = task.priority

    override fun compareTo(other: DownloadComparatorTask): Int {
        return when {
            priority < other.priority -> -1
            priority > other.priority -> 1
            else -> 0
        }
    }

    class DownloadRunnable(
        private val task: DownloadTask,
        private val repository: Repository
    ) : Runnable {
        override fun run() {
            Thread.sleep(3000)
            //下载完成后 发送对应广播
            AppHolder.localBroadcastManager().sendBroadcast(Intent().apply {
                putExtra(FINISH_NODE, DownloadNode(task.gid, task.token, task.page))
                action = DownloadService.BROADCAST_ACTION
            })
        }
    }
}


//通过扩展方法添加任务
fun ExecutorService.submitDownload(repository: Repository, task: DownloadTask) {
    execute(DownloadComparatorTask(repository, task))
}


fun Context.startGalleyDownload(message: DownloadMessage) {
    Intent(this, DownloadService::class.java).apply {
        action = DownloadService.ACTION_DOWNLOAD
        putExtra(DownloadService.DOWNLOAD_TASK, message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(this)
        } else {
            startService(this)
        }
    }
}

fun Context.startAllGalleryDownload() {
    Intent(this, DownloadService::class.java).apply {
        action = DownloadService.ACTION_START_ALL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(this)
        } else {
            startService(this)
        }
    }
}

fun Context.stopAllGalleryDownload() {
    Intent(this, DownloadService::class.java).apply {
        action = DownloadService.ACTION_STOP_ALL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(this)
        } else {
            startService(this)
        }
    }
}


