package com.mitsuki.ehit.ui.download.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mitsuki.ehit.model.entity.DownloadNode
import com.mitsuki.ehit.model.entity.DownloadTask
import java.util.concurrent.*

class DownloadService : Service() {

    companion object {
        const val DOWNLOAD_TASK = "DOWNLOAD_TASK"
    }


    //    如果service被干掉，所有的下载任务会停止，这里只记录下载中的
    private val downloadPriority: MutableMap<String, DownloadNode> by lazy { hashMapOf() }


//    private val downloadPool :ExecutorService by lazy {
//        ThreadPoolExecutor(3,3,0L, TimeUnit.MINUTES, PriorityBlockingQueue(1,ssss()))
//    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //尽量使用前台服务打开service
        //首先需要在这里响应通知栏
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //在此处接收DownloadTask
        //接收到后需要进一步处理，可能还需要为此更新通知栏

        intent?.getParcelableExtra<DownloadTask>(DOWNLOAD_TASK)?.also { task ->

            (downloadPriority[task.key] ?: DownloadNode(task.gid, task.token).also { node ->
                downloadPriority[task.key] = node
            }).also { node ->
                node.append()

            }


            //这里作为统一的注料口
            //当任务进来 生成一个下载原子列表，然后往下载器的下料口注入
            //下载器的下料口需要自动归类数据，以gid-token作为key的形式
            //下载器是固定线程数量线程池，
            //需要维护一个下载队列的优先度

            //收到task 生成 atom 列表 加入到node中，然后再投入线程池


        }

        return super.onStartCommand(intent, flags, startId)
    }
}