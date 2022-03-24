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

        private const val TAG = "Download"


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
    private var workStates = true
    private val mLoopLock = Mutex()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        DownloadBroadcast.registerReceiver(this, mReceiver)
        startWork()
    }

    override fun onDestroy() {
        super.onDestroy()
        runBlocking { downloadSchedule.cancelAll() }
        workStates = false
        DownloadBroadcast.unregisterReceiver(this, mReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_ALL -> {
                notify.replyForeground(this)
                startAll()
            }
            ACTION_DOWNLOAD -> {
                notify.replyForeground(this)
                intent.getParcelableExtra<DownloadMessage>(DOWNLOAD_TASK)?.apply { postTask(this) }
            }
            ACTION_RESTART -> {
                notify.replyForeground(this)
                (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
                    restart(first as Long, second as String)
                }
            }
        }
        return START_STICKY
    }

    private fun postTask(message: DownloadMessage) {
        Log.d(TAG, "new task:$message")
        CoroutineScope(Dispatchers.Default).launch {
            val newNode = downloadDao.updateDownloadList(message) //通过数据库对比获取差分数据
//            downloadSchedule.thumb(message.gid, message.token)
            downloadSchedule.append(message.key, newNode)
            tryUnlock()
            withContext(Dispatchers.Main) {
                //更新通知
            }
        }
    }


    private fun stopAll() {
        Log.d(TAG, "stop all task")
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.cancelAll()
        }
    }

    private fun startAll() {
        //从数据库中查询出所有需要下载的任务
        //插入新任务需要统计差分
        Log.d(TAG, "start all task")
        CoroutineScope(Dispatchers.Default).launch {
            val infoList = downloadDao.queryALlDownloadInfo()
            val result = hashMapOf<String, List<DownloadNode>>()

            infoList.forEach {
                if (it.localThumb.isEmpty()) {
//                    downloadSchedule.thumb(it.gid, it.token)
                }
                downloadDao.queryDownloadNode(it.gid, it.token).apply {
                    if (isNotEmpty()) {
                        result[DownloadMessage.key(it.gid, it.token)] = this
                    }
                }
            }
            downloadSchedule.append(result)
            tryUnlock()
            withContext(Dispatchers.Main) {
                //显示通知
            }
        }
    }


    private fun restart(gid: Long, token: String) {
        Log.d(TAG, "restart task:$gid-$token")
        //查询数据重新插入
        CoroutineScope(Dispatchers.Default).launch {
//            downloadSchedule.thumb(gid, token)
            downloadSchedule.append(
                DownloadMessage.key(gid, token),
                downloadDao.queryDownloadNode(gid, token)
            )
            tryUnlock()
        }
    }

    private fun stop(gid: Long, token: String) {
        Log.d(TAG, "stop task:$gid-$token")
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.cancel(DownloadMessage.key(gid, token))
        }
    }

    private fun startWork() {
        CoroutineScope(Dispatchers.Default).launch {
            while (workStates) {
                //在这里执行下载循环，在没有数据的时候发送一个下载完成的通知然后进行阻塞直到下次一数据到来
                Log.d(TAG, "work loop step start")
                mLoopLock.lock()
                Log.d(TAG, "work loop step download")
                val result = downloadSchedule.singleWork()
                tryUnlock()
                if (result) {
                    //下一个
                    Log.d(TAG, "next work")
                } else {
                    Log.d(TAG, "list is empty")
                    //任务全结束了
                    launch(Dispatchers.Main) {
                        notify.notifyFinish(this@DownloadService)
                        notify.reset()
                        stopForeground(false)
                    }
                    //然后阻塞住
                    mLoopLock.lock()
                }
            }
        }
    }

    private fun tryUnlock() {
        Log.d(TAG, "try unlock to start task")
        try {
            mLoopLock.unlock()
        } catch (err: IllegalStateException) {
            err.printStackTrace()
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                DownloadBroadcast.DOWNLOAD_BROADCAST_PAGE_START -> {
                    val name = intent.getStringExtra(DownloadBroadcast.TASK_NAME) ?: ""
                    val total = intent.getIntExtra(DownloadBroadcast.TASK_TOTAL, 0)
                    val over = intent.getIntExtra(DownloadBroadcast.TASK_OVER, 0)
                    notify.notifyUpdate(this@DownloadService, name, "${total - over}/$total")
                }
                DownloadBroadcast.DOWNLOAD_BROADCAST_PAGE -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        intent.getParcelableExtra<DownloadNode>(DownloadBroadcast.FINISH_NODE)
                            ?.apply { downloadDao.updateDownloadNode(this) }
                        //更新数据
                        withContext(Dispatchers.Main) {
                            //更新通知
                        }
                    }
                }
                DownloadBroadcast.DOWNLOAD_BROADCAST_THUMB -> {

                }
                DownloadBroadcast.DOWNLOAD_BROADCAST_STOP -> (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
                    stop(first as Long, second as String)
                }
                DownloadBroadcast.DOWNLOAD_BROADCAST_STOP_ALL -> stopAll()
            }
        }
    }
}