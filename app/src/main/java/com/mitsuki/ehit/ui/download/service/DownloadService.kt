package com.mitsuki.ehit.ui.download.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.ehit.crutch.RoadGate
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.download.DownloadScheduler
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.internal.notify
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    companion object {

        private const val TAG = "EDownload"

        const val ACTION_START_ALL = "START_ALL"
        const val ACTION_DOWNLOAD = "DOWNLOAD"
        const val ACTION_RESTART = "RESTART"

        const val DOWNLOAD_TASK = "DOWNLOAD_TASK"
        const val TARGET = "TARGET"

        fun startDownload(context: Context, msg: DownloadMessage) {
            Intent(context, DownloadService::class.java).apply {
                action = ACTION_DOWNLOAD
                putExtra(DOWNLOAD_TASK, msg)
                ContextCompat.startForegroundService(context, this)
            }
        }

        fun restartDownload(context: Context, gid: Long, token: String) {
            Intent(context, DownloadService::class.java).apply {
                action = ACTION_RESTART
                putExtra(TARGET, gid to token)
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

    @RemoteRepository
    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var downloadDao: DownloadDao

    @Inject
    lateinit var notify: DownloadNotify

    private val downloadSchedule by lazy { DownloadScheduler(repository) }
    private val mReceiver by lazy { MyBroadcastReceiver() }

    @Volatile
    private var workStates = false
    private val workLock = Mutex()

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
        workStates = false
        DownloadBroadcast.unregisterReceiver(this, mReceiver)
    }

    private suspend fun startWork() {
        workLock.withLock {
            if (workStates) return
            workStates = true
        }
        while (workStates) {
            Log.d(TAG, "work loop step start")
            val result = downloadSchedule.singleWork()
            if (result) {
                Log.d(TAG, "next work")
            } else {
                Log.d(TAG, "list is empty")
                withContext(Dispatchers.Main) {
                    notify.notifyFinish(this@DownloadService)
                    stopForeground(false)
                    stopSelf()
                }
            }
        }
    }

    private fun handleEvent(intent: Intent?) {
        when (intent?.action) {
            ACTION_DOWNLOAD -> {
                intent.getParcelableExtra<DownloadMessage>(DOWNLOAD_TASK)?.apply { postTask(this) }
            }
            ACTION_START_ALL -> {
                startAll()
            }
            ACTION_RESTART -> {
                (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
                    restart(first as Long, second as String)
                }
            }
            DownloadBroadcast.DOWNLOAD_BROADCAST_PAGE -> {
                CoroutineScope(Dispatchers.Default).launch {
                    intent.getParcelableExtra<DownloadNode>(DownloadBroadcast.FINISH_NODE)
                        ?.apply { downloadDao.updateDownloadNode(this) }
                }
                val name = intent.getStringExtra(DownloadBroadcast.TASK_NAME) ?: ""
                val total = intent.getIntExtra(DownloadBroadcast.TASK_TOTAL, 0)
                val over = intent.getIntExtra(DownloadBroadcast.TASK_OVER, 0)
                notify.notifyUpdate(this@DownloadService, name, total, over)

            }
            DownloadBroadcast.DOWNLOAD_BROADCAST_STOP -> (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
                stop(first as Long, second as String)
            }
            DownloadBroadcast.DOWNLOAD_BROADCAST_STOP_ALL -> stopAll()
        }
    }


    private fun startAll() {
        Log.d(TAG, "start all task")
        CoroutineScope(Dispatchers.Default).launch {
            val infoList = downloadDao.queryALlDownloadInfo()
            val result = hashMapOf<String, List<DownloadNode>>()

            infoList.forEach {
                if (it.localThumb.isEmpty()) {
                    launch(Dispatchers.IO) { downloadSchedule.thumb(it.gid, it.token) }
                }
                downloadDao.queryDownloadNode(it.gid, it.token).apply {
                    if (isNotEmpty()) {
                        result[DownloadMessage.key(it.gid, it.token)] = this
                    }
                }
            }
            downloadSchedule.append(result)
            startWork()
        }
    }

    private fun restart(gid: Long, token: String) {
        Log.d(TAG, "restart task:$gid-$token")
        CoroutineScope(Dispatchers.Default).launch {
            launch(Dispatchers.IO) { downloadSchedule.thumb(gid, token) }
            downloadSchedule.append(
                DownloadMessage.key(gid, token),
                downloadDao.queryDownloadNode(gid, token)
            )
            startWork()
        }
    }

    private fun postTask(message: DownloadMessage) {
        Log.d(TAG, "new task:$message")
        CoroutineScope(Dispatchers.Default).launch {
            val newNode = downloadDao.updateDownloadList(message) //通过数据库对比获取差分数据
            launch(Dispatchers.IO) { downloadSchedule.thumb(message.gid, message.token) }
            downloadSchedule.append(message.key, newNode)
            startWork()
        }
    }

    private fun stopAll() {
        Log.d(TAG, "stop all task")
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.cancelAll()
            stopSelf()
        }
    }

    private fun stop(gid: Long, token: String) {
        Log.d(TAG, "stop task:$gid-$token")
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.cancel(DownloadMessage.key(gid, token))
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            handleEvent(intent)
        }
    }
}