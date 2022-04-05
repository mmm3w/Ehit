package com.mitsuki.ehit.model.download

import com.mitsuki.ehit.crutch.uils.tryUnlock
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


class DownloadWork<T>(
    private val maxCount: Int,
    list: List<T> = emptyList(),
    val action: suspend (T, Int, DownloadWork<T>) -> Unit
) {
    private var mTotal = list.size
    private val mData: LinkedList<T> = LinkedList(list)
    private val mDataLock = Mutex()

    var down = 0
        private set
    private val downLock = Mutex()

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
                    action(item, mTotal, this@DownloadWork)
                    downLock.withLock { down++ }
                    count.getAndDecrement()
                    mBlockLock.tryUnlock()
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
        mBlockLock.tryUnlock()
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