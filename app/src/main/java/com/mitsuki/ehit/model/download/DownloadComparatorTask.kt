package com.mitsuki.ehit.model.download

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.entity.DownloadPriority
import com.mitsuki.ehit.model.entity.DownloadTask
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.ui.download.service.DownloadService
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

class DownloadComparatorTask(repository: Repository, priority: DownloadPriority) :
    FutureTask<Any>(DownloadRunnable(priority, repository), null),
    Comparable<DownloadComparatorTask> {

    private val priority: Long = priority.priority

    override fun compareTo(other: DownloadComparatorTask): Int {
        return when {
            priority < other.priority -> -1
            priority > other.priority -> 1
            else -> 0
        }
    }

    class DownloadRunnable(
        private val priority: DownloadPriority,
        private val repository: Repository
    ) : Runnable {
        override fun run() {
            Log.d("Download", "$priority")
            Thread.sleep(3000)
            //下载完成后 发送对应广播
            AppHolder.localBroadcastManager().sendBroadcast(Intent())
        }
    }
}


//通过扩展方法添加任务
fun ExecutorService.submitDownload(repository: Repository, priority: DownloadPriority) {
    execute(DownloadComparatorTask(repository, priority))
}


fun Context.startGalleyDownload(task: DownloadTask) {
    Intent(this, DownloadService::class.java).apply {
        putExtra(DownloadService.DOWNLOAD_TASK, task)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(this)
        } else {
            startService(this)
        }
    }
}

fun Context.startAllGalleryDownload() {
    Intent(this, DownloadService::class.java).apply {
        putExtra(DownloadService.START_ALL, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(this)
        } else {
            startService(this)
        }
    }
}

fun Context.stopAllGalleryDownload() {
    Intent(this, DownloadService::class.java).apply {
        putExtra(DownloadService.STOP_ALL, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(this)
        } else {
            startService(this)
        }
    }
}


