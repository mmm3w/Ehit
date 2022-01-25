package com.mitsuki.ehit.ui.download.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.download.DownloadCache
import com.mitsuki.ehit.model.download.submitDownload
import com.mitsuki.ehit.model.entity.DownloadTask
import com.mitsuki.ehit.model.repository.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    companion object {
        const val DOWNLOAD_TASK = "DOWNLOAD_TASK"
        const val STOP_ALL = "STOP_ALL"
        const val START_ALL = "START_ALL"
    }

    @RemoteRepository
    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var downloadDao: DownloadDao

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
            .registerReceiver(mReceiver, IntentFilter("DOWNLOAD"))
        //尽量使用前台服务打开service
        //首先需要在这里响应通知栏
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //在此处接收DownloadTask
        //接收到后需要进一步处理，可能还需要为此更新通知栏
        val stopAll = intent?.getBooleanExtra(STOP_ALL, false) ?: false
        val startAll = intent?.getBooleanExtra(START_ALL, false) ?: false
        val task = intent?.getParcelableExtra<DownloadTask>(DOWNLOAD_TASK)
        when {
            stopAll -> stopAll()
            startAll -> startAll()
            task != null -> postTask(task)
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun stopAll() {
        workQueue.clear()
        downloadSchedule.clear()
        //最后再弹个通知
    }

    private fun startAll() {
        //从数据库中查询出所有需要下载的任务
        //插入新任务需要统计差分
        runBlocking(Dispatchers.Default) {
            val infoList = downloadDao.queryALlDownloadInfo()
            infoList.forEach {


            }
        }
    }

    private fun postTask(task: DownloadTask) {
        runBlocking(Dispatchers.Default) {
            val newNode = downloadDao.updateDownloadList(task) //通过数据库对比获取差分数据
            downloadSchedule.append(task, newNode) //再和内存的数据做对比获取需要放入线程池下载的查分数据
                .forEach { downloadPool.submitDownload(repository, it) }
            //在获取差分数据的过程中均会更新相应数据
        }
    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //处理下载完成的广播
            Log.d("Download", "广播")
        }
    }
}