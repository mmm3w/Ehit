package com.mitsuki.ehit.ui.download.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
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
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    companion object {
        const val ACTION_START_ALL = "START_ALL"
        const val ACTION_STOP_ALL = "STOP_ALL"
        const val ACTION_DOWNLOAD = "DOWNLOAD"
        const val ACTION_RESTART = "RESTART"
        const val ACTION_STOP = "STOP"

        const val DOWNLOAD_TASK = "DOWNLOAD_TASK"
        const val TARGET = "TARGET"

        const val FINISH_NODE = "FINISH_NODE"

        const val NOTIFICATION_CHANNEL = "DOWNLOAD"
        const val BROADCAST_ACTION = "DOWNLOAD_ACTION"

        const val NOTIFICATION_ID = 10002

        fun startDownload(context: Context, msg: DownloadMessage) {
            Intent(context, DownloadService::class.java).apply {
                action = ACTION_DOWNLOAD
                putExtra(DOWNLOAD_TASK, msg)
                startService(context, this)
            }
        }

        fun stopDownload(context: Context, gid: Long, token: String) {
            Intent(context, DownloadService::class.java).apply {
                action = ACTION_STOP
                putExtra(TARGET, gid to token)
                startService(context, this)
            }
        }

        fun restartDownload(context: Context, gid: Long, token: String) {
            Intent(context, DownloadService::class.java).apply {
                action = ACTION_RESTART
                putExtra(TARGET, gid to token)
                startService(context, this)
            }
        }

        fun startAllDownload(context: Context) {
            startService(context, Intent(context, DownloadService::class.java).apply {
                action = ACTION_START_ALL
            })
        }

        fun stopAllDownload(context: Context) {
            startService(context, Intent(context, DownloadService::class.java).apply {
                action = ACTION_STOP_ALL
            })
        }

        private fun startService(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    @RemoteRepository
    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var downloadDao: DownloadDao

    @Inject
    lateinit var helper: NotificationHelper

    private val downloadSchedule by lazy { DownloadScheduler() }

    private val mReceiver by lazy { MyBroadcastReceiver() }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mReceiver, IntentFilter(BROADCAST_ACTION))
        //响应前台通知
        helper.startForeground(this, NOTIFICATION_CHANNEL, NOTIFICATION_ID) {
            it.setSmallIcon(android.R.drawable.stat_sys_download)
            it.setContentTitle("下载列表")
        }

        downloadSchedule.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
        downloadSchedule.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_ALL -> startAll()
            ACTION_STOP_ALL -> stopAll()
            ACTION_DOWNLOAD ->
                intent.getParcelableExtra<DownloadMessage>(DOWNLOAD_TASK)?.apply { postTask(this) }
            ACTION_RESTART -> (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
                restart(first as Long, second as String)
            }
            ACTION_STOP -> (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
                stop(first as Long, second as String)
            }
        }
        return START_STICKY
    }


    private fun stopAll() {
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.cancelAll()
        }
    }

    private fun startAll() {
        //从数据库中查询出所有需要下载的任务
        //插入新任务需要统计差分
        CoroutineScope(Dispatchers.Default).launch {
            val infoList = downloadDao.queryALlDownloadInfo()
            val result = ArrayList<Pair<String, List<DownloadNode>>>()
            infoList.forEach {
                downloadDao.queryDownloadNode(it.gid, it.token).apply {
                    if (isNotEmpty()) {
                        result.add(
                            DownloadMessage.key(it.gid, it.token) to this
                        )
                    }
                }
            }
            downloadSchedule.append(result)
            withContext(Dispatchers.Main) {
                //显示通知
            }
        }
    }

    private fun postTask(message: DownloadMessage) {
        CoroutineScope(Dispatchers.Default).launch {
            val newNode = downloadDao.updateDownloadList(message) //通过数据库对比获取差分数据
            downloadSchedule.append(message.key, newNode)
            withContext(Dispatchers.Main) {
                //更新通知
            }
        }
    }

    private fun restart(gid: Long, token: String) {
        //查询数据重新插入
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.append(
                DownloadMessage.key(gid, token),
                downloadDao.queryDownloadNode(gid, token)
            )
        }
    }

    private fun stop(gid: Long, token: String) {
        CoroutineScope(Dispatchers.Default).launch {
            downloadSchedule.cancel(DownloadMessage.key(gid, token))
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //处理下载完成的广播
            CoroutineScope(Dispatchers.Default).launch {
                downloadSchedule.idle()
                intent?.getParcelableExtra<DownloadNode>(FINISH_NODE)?.apply {
                    downloadDao.updateDownloadNode(this)
                }
                //更新数据
                withContext(Dispatchers.Main) {
                    //更新通知
                }
            }
        }
    }
}