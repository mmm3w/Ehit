package com.mitsuki.ehit.model.download

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.math.max


/**
 * 单个下载任务
 * 尝试使用协程去处理
 *
 * 首先处理并发数
 */
class DownloadWork<T>(
    private val maxCount: Int,
    list: List<T> = emptyList(),
    val action: suspend (T, Int, Int) -> Unit
) {
    private val mData: LinkedList<T> = LinkedList(list)
    private var mTotal = list.size
    private val mDataLock = Mutex()
    private var count = AtomicInteger(0)
    private val mBlockLock = Mutex()
    private val mChannel = Channel<T>()

    private val mIsStarted: AtomicBoolean = AtomicBoolean(false)

    val isStarted = mIsStarted.get()

    fun exec() {
        if (isStarted) return
        mIsStarted.getAndSet(true)
        loop()
        runBlocking {
            for (item in mChannel) {
                launch(Dispatchers.IO) {
                    action(item, mTotal, mData.size)
                    count.getAndDecrement()
                    try {
                        mBlockLock.unlock()
                    } catch (err: IllegalStateException) {
                    }
                }
            }
        }
        mIsStarted.getAndSet(false)
    }

    suspend fun append(data: T) {
        mDataLock.withLock {
            mData.add(data)
            mTotal++
        }
    }

    suspend fun append(data: List<T>) {
        mDataLock.withLock {
            mData.addAll(data)
            mTotal += data.size
        }
    }

    suspend fun stop() {
        //清除残余数据
        mDataLock.withLock {
            mData.clear()
        }
        count.set(0)
        //释放锁 结束loop 结束exec
        try {
            mBlockLock.unlock()
        } catch (err: IllegalStateException) {
        }
    }

    private fun loop() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                try {
                    mDataLock.withLock { mChannel.send(mData.remove()) }
                    val c = count.incrementAndGet()
                    if (c >= maxCount) {
                        mBlockLock.lock()
                    }
                } catch (err: NoSuchElementException) {
                    mChannel.close()
                    break
                }
            }
        }
    }
}