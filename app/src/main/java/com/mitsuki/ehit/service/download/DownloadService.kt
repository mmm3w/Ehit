package com.mitsuki.ehit.service.download

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mitsuki.ehit.const.DataKey
import com.mitsuki.ehit.model.entity.DownloadMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    companion object {

//        private const val TAG = "EDownload"


//        const val DOWNLOAD_TASK = "DOWNLOAD_TASK"
//        const val TARGET = "TARGET"


    }

//    @RemoteRepository
//    @Inject
//    lateinit var repository: Repository
//
//    @Inject
//    lateinit var downloadDao: DownloadDao
//

//
//    @Inject
//    lateinit var shareData: ShareData

    //    private val downloadSchedule by lazy { DownloadScheduler(repository) }


    @Inject
    lateinit var notify: DownloadNotify

    @Inject
    lateinit var downloadManager: DownloadManager


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
        DownloadBroadcast.unregisterReceiver(this, mReceiver)
        downloadManager.destroy()
    }

//    private suspend fun startWork() {
////        workLock.withLock {
////            if (workStates) return
////            workStates = true
////        }
////        while (workStates) {
////            Log.d(TAG, "work loop step start")
////            val result = downloadSchedule.singleWork()
////            if (result) {
////                Log.d(TAG, "next work")
////            } else {
////                Log.d(TAG, "list is empty")
////                withContext(Dispatchers.Main) {
////                    notify.notifyFinish(this@DownloadService)
////                    stopForeground(false)
////                    stopSelf()
////                }
////            }
////        }
//    }

    private fun handleEvent(intent: Intent?) {
        when (intent?.action) {
            //新的下载任务
            DownloadEvent.ACTION_DOWNLOAD -> {
                intent.getParcelableExtra<DownloadMessage>(DataKey.DOWNLOAD_TASK)
                    ?.apply { downloadManager.postTask(this) }
            }
            //下载结束
            DownloadBroadcast.DOWNLOAD_FINISH -> {
                notify.notifyFinish(this@DownloadService)
                stopForeground(false)
                stopSelf()
            }
            //全部停止
            DownloadEvent.ACTION_START_ALL -> {
                downloadManager.stopAll()
                stopForeground(false)
                stopSelf()
            }
            DownloadEvent.ACTION_RESTART -> {
//                (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
//                    restart(first as Long, second as String)
//                }
            }
            DownloadBroadcast.DOWNLOAD_BROADCAST_PAGE -> {
//                CoroutineScope(Dispatchers.Default).launch {
//                    intent.getParcelableExtra<DownloadNode>(DownloadBroadcast.FINISH_NODE)
//                        ?.apply { downloadDao.updateDownloadNode(this) }
//                }
//                val name = intent.getStringExtra(DownloadBroadcast.TASK_NAME) ?: ""
//                val total = intent.getIntExtra(DownloadBroadcast.TASK_TOTAL, 0)
//                val over = intent.getIntExtra(DownloadBroadcast.TASK_OVER, 0)
//                notify.notifyUpdate(this@DownloadService, name, total, over)

            }
            DownloadBroadcast.DOWNLOAD_BROADCAST_STOP -> {
//                (intent.getSerializableExtra(TARGET) as? Pair<*, *>)?.apply {
//                    stop(first as Long, second as String)
//                }
            }
            DownloadBroadcast.DOWNLOAD_BROADCAST_STOP_ALL -> {
//                stopAll()
            }


        }
    }

//
//    private fun startAll() {
//        Log.d(TAG, "start all task")
//        CoroutineScope(Dispatchers.Default).launch {
//            val infoList = downloadDao.queryALlDownloadInfo()
//            val result = hashMapOf<String, List<DownloadNode>>()
//
//            infoList.forEach {
//                if (it.localThumb.isEmpty()) {
//                    launch(Dispatchers.IO) { downloadSchedule.thumb(it.gid, it.token) }
//                }
//                downloadDao.queryDownloadNode(it.gid, it.token).apply {
//                    if (isNotEmpty()) {
//                        result[DownloadMessage.key(it.gid, it.token)] = this
//                    }
//                }
//            }
//            downloadSchedule.append(result, shareData.spDownloadThread)
//            startWork()
//        }
//    }
//
//    private fun restart(gid: Long, token: String) {
//        Log.d(TAG, "restart task:$gid-$token")
//        CoroutineScope(Dispatchers.Default).launch {
//            launch(Dispatchers.IO) { downloadSchedule.thumb(gid, token) }
//            downloadSchedule.append(
//                DownloadMessage.key(gid, token),
//                downloadDao.queryDownloadNode(gid, token),
//                shareData.spDownloadThread
//            )
//            startWork()
//        }
//    }
//
//    private fun postTask(message: DownloadMessage) {
////        Log.d(TAG, "new task:$message")
////        CoroutineScope(Dispatchers.Default).launch {
////            val newNode = downloadDao.updateDownloadList(message) //通过数据库对比获取差分数据
////            launch(Dispatchers.IO) { downloadSchedule.thumb(message.gid, message.token) }
////            downloadSchedule.append(message.key, newNode, shareData.spDownloadThread)
////            startWork()
////        }
//    }
//
//    private fun stopAll() {
//        Log.d(TAG, "stop all task")
//        CoroutineScope(Dispatchers.Default).launch {
//            downloadSchedule.cancelAll()
//            stopSelf()
//        }
//    }
//
//    private fun stop(gid: Long, token: String) {
//        Log.d(TAG, "stop task:$gid-$token")
//        CoroutineScope(Dispatchers.Default).launch {
//            downloadSchedule.cancel(DownloadMessage.key(gid, token))
//        }
//    }

    private inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            handleEvent(intent)
        }
    }
}