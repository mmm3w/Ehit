package com.mitsuki.ehit.crutch.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class BlockWork<T>(
    maxCount: Int,
    list: List<T> = emptyList(),
    val action: suspend (T) -> Unit
) {
    private val mWorkLock = Mutex()
    private var mLoopJob: Job? = null

    private val stabilizer: Stabilizer = Stabilizer(maxCount)

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
                        stabilizer.release()
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
                stabilizer.stuck()
                mDataLock.withLock {
                    try {
                        mChannel.send(mData.remove())
                    } catch (err: NoSuchElementException) {
                        isDeath.getAndSet(true)
                        mChannel.close()
                    }
                }
            }
        }
    }
}