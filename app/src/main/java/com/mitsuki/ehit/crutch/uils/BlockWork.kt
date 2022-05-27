package com.mitsuki.ehit.crutch.uils

import com.mitsuki.ehit.crutch.extensions.tryUnlock
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class BlockWork<T>(
    private val maxCount: Int,
    list: List<T> = emptyList(),
    val action: suspend (T) -> Unit
) {
    private val mWorkLock = Mutex()
    private var mLoopJob: Job? = null

    private var count = AtomicInteger(0)
    private var mCountLock = Mutex()

    private val mChannel = Channel<T>()
    private val mData: LinkedList<T> = LinkedList(list)
    private val mDataLock = Mutex()

    private var isDeath: AtomicBoolean = AtomicBoolean(false)

    fun exec() {
        runBlocking {
            mWorkLock.withLock {
                if (isDeath.get()) return@withLock
                mLoopJob?.cancel()
                mLoopJob = loop()

                for (item in mChannel) {
                    launch(Dispatchers.Default) {
                        action(item)
                        count.getAndDecrement()
                        mCountLock.tryUnlock()
                        mCountLock.lock()
                    }
                }
            }
        }
    }

    suspend fun append(data: T): Boolean {
        return mDataLock.withLock {
            if (isDeath.get()) {
                false
            } else {
                mData.add(data)
                true
            }
        }
    }

    suspend fun append(data: List<T>): Boolean {
        return mDataLock.withLock {
            if (isDeath.get()) {
                false
            } else {
                mData.addAll(data)
                true
            }
        }
    }

    suspend fun stop() {
        mDataLock.withLock {
            isDeath.getAndSet(true)
            mLoopJob?.cancel()
            mChannel.close()
            mData.clear()
        }
    }

    private fun loop(): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (!isDeath.get()) {
                val c = count.get()
                if (c >= maxCount) {
                    //这里要卡住
                    mCountLock.withLock { /* just lock */ }
                } else {
                    mDataLock.withLock {
                        try {
                            mChannel.send(mData.remove())
                            count.incrementAndGet()
                            mCountLock.tryUnlock()
                        } catch (err: NoSuchElementException) {
                            isDeath.getAndSet(true)
                            mChannel.close()
                        }
                    }
                }
            }
        }
    }
}