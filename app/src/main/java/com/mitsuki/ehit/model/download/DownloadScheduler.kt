package com.mitsuki.ehit.model.download

import com.mitsuki.ehit.model.entity.db.DownloadNode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DownloadScheduler {
    private val queue = DownloadQueue(3)
    private val threadPool: ExecutorService = Executors.newCachedThreadPool()
    private var loopThread: Thread? = null

    fun start() {
        if (loopThread?.isAlive == true) return
        loopThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    threadPool.submit(DownloadRunnable(queue.take()))
                } catch (inner: InterruptedException) {
                    inner.printStackTrace()
                }
            }
        }.apply { start() }
    }

    fun stop() {
        loopThread?.interrupt()
        loopThread = null
        threadPool.shutdown()
    }

    fun append(tag:String, newNode: List<DownloadNode>) {
        queue.put(tag, newNode)
    }

    fun append(data: List<Pair<String, List<DownloadNode>>>) {
        queue.put(data)
    }

    fun maxParallelTask(c: Int) {
        queue.setMaxCount(c)
    }

    fun cancelAll() {
        queue.clear()
    }

    fun cancel(tag: String) {
        queue.cancel(tag)
    }

    fun idle() {
        queue.idle()
    }
}