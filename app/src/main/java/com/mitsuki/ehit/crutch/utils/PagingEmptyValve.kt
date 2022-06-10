package com.mitsuki.ehit.crutch.utils

import androidx.lifecycle.Lifecycle
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import com.mitsuki.ehit.crutch.extensions.justLock
import com.mitsuki.ehit.crutch.extensions.tryUnlock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

/**
 * 主要用于在刷新事件之间插入一个PagingData.empty()使列表显示为空
 */
class PagingEmptyValve {

    private val mLock = Mutex()

    suspend fun <T : Any> submitData(
        lifecycle: Lifecycle,
        adapter: PagingDataAdapter<T, *>,
        pagingData: PagingData<T>
    ) {
        withContext(Dispatchers.Default) {
            if (mLock.isLocked) {
                adapter.submitData(lifecycle, PagingData.empty())
                launch {
                    //某些状态下PagingData.empty不会触发加载事件，所以自动进行解锁
                    delay(200)
                    //自动释放
                    mLock.tryUnlock()
                }
                mLock.justLock()
                adapter.submitData(lifecycle, pagingData)
            } else {
                adapter.submitData(lifecycle, pagingData)
            }
        }
    }

    fun enable() {
        runBlocking {
            if (!mLock.isLocked) {
                mLock.lock()
            }
        }
    }

    //非empty的loadStates，需要处理相关事件
    fun emptyStates(loadStates: LoadStates): Boolean {
        return loadStates.isEmptyStates().also { es ->
            if (es) {
                mLock.tryUnlock()
            }
        }
    }

    //基于internal特性调用，随时关注api的变动，此处可能需要改为反射实现
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    private fun LoadStates.isEmptyStates(): Boolean {
        return this === androidx.paging.PageEvent.Insert.EMPTY_REFRESH_LOCAL.sourceLoadStates
    }
}