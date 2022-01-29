package com.mitsuki.ehit.ui.download.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.download.DownloadCache
import com.mitsuki.ehit.model.download.submitDownload
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.entity.db.DownloadNode
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.ExecutorService
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
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
    }

    @RemoteRepository
    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var downloadDao: DownloadDao

    @Inject
    lateinit var helper: NotificationHelper

    private val workQueue: PriorityBlockingQueue<Runnable> by lazy { PriorityBlockingQueue(64) }

    private val downloadSchedule by lazy { DownloadCache() }

    private val mReceiver by lazy { MyBroadcastReceiver() }

    private var downloadPool: ExecutorService =
        ThreadPoolExecutor(3, 3, 0L, TimeUnit.MINUTES, workQueue)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
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
        workQueue.clear()
        downloadSchedule.clear()
        //最后再弹个通知
    }

    private fun startAll() {
        //从数据库中查询出所有需要下载的任务
        //插入新任务需要统计差分
        CoroutineScope(Dispatchers.Default).launch {
            val infoList = downloadDao.queryALlDownloadInfo()
            infoList.forEach {


            }

            withContext(Dispatchers.Main) {
                //显示通知
            }
        }
    }

    private fun postTask(message: DownloadMessage) {
        CoroutineScope(Dispatchers.Default).launch {
            val newNode = downloadDao.updateDownloadList(message) //通过数据库对比获取差分数据
            downloadSchedule.append(message, newNode) //再和内存的数据做对比获取需要放入线程池下载的查分数据
                .forEach { downloadPool.submitDownload(repository, it) }
            //在获取差分数据的过程中均会更新相应数据

            withContext(Dispatchers.Main) {
                //更新通知
            }
        }
    }

    private fun restart(gid: Long, token: String) {


    }

    private fun stop(gid: Long, token: String) {


    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //处理下载完成的广播
            CoroutineScope(Dispatchers.Default).launch {
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