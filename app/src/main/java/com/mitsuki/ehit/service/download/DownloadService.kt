package com.mitsuki.ehit.service.download

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.entity.DownloadMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    @Inject
    lateinit var downloadDao: DownloadDao

    @Inject
    lateinit var notify: DownloadNotify

    @Inject
    lateinit var downloadManager: DownloadManager

    private var mProgressJob: Job? = null

    private val mReceiver by lazy { MyBroadcastReceiver() }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notify.replyForeground(this)
        DownloadBroadcast.registerReceiver(this, mReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleEvent(intent)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgressJob?.cancel()
        DownloadBroadcast.unregisterReceiver(this, mReceiver)
        downloadManager.destroy()

        //TODO 这里重置
    }

    private fun handleEvent(intent: Intent?) {
        when (intent?.action) {
            DownloadEvent.ACTION_DOWNLOAD -> {
                intent.getParcelableExtra<DownloadMessage>(DataKey.DOWNLOAD_TASK)
                    ?.apply { downloadManager.postTask(this) }
            }
            DownloadEvent.ACTION_START_ALL -> {
                downloadManager.startAll()
            }
            DownloadEvent.ACTION_RESTART -> {
                val gid = intent.getLongExtra(DataKey.GALLERY_ID, -1)
                val token = intent.getStringExtra(DataKey.GALLERY_TOKEN)
                if (gid != -1L && !token.isNullOrEmpty()) {
                    downloadManager.startTask(gid, token)
                }
            }
            DownloadBroadcast.DOWNLOAD_START -> {
                val gid = intent.getLongExtra(DataKey.GALLERY_ID, -1)
                val token = intent.getStringExtra(DataKey.GALLERY_TOKEN)
                if (gid != -1L && !token.isNullOrEmpty()) {
                    downloadProgressNotifyListen(gid, token)
                }
            }
            DownloadBroadcast.DOWNLOAD_FINISH -> {
                mProgressJob?.cancel()
                notify.notifyFinish(this@DownloadService)
                stopForeground(false)
                stopSelf()
            }
            DownloadBroadcast.DOWNLOAD_STOP -> {
                val gid = intent.getLongExtra(DataKey.GALLERY_ID, -1)
                val token = intent.getStringExtra(DataKey.GALLERY_TOKEN)
                if (gid != -1L && !token.isNullOrEmpty()) {
                    downloadManager.stopTask(gid, token)
                }
            }
            DownloadBroadcast.DOWNLOAD_STOP_ALL -> {
                downloadManager.stopAll()
                mProgressJob?.cancel()
                notify.notifyFinish(this@DownloadService)
                stopForeground(false)
                stopSelf()
            }


        }
    }

    private fun downloadProgressNotifyListen(gid: Long, token: String) {
        mProgressJob?.cancel()
        mProgressJob = CoroutineScope(Dispatchers.Main).launch {
            downloadDao.galleryDownloadProgress(gid, token).collect {
                notify.notifyUpdate(this@DownloadService, it.title, it.total, it.completed)
            }
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            handleEvent(intent)
        }
    }
}