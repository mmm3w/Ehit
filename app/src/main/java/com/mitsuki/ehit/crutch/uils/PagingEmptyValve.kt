package com.mitsuki.ehit.crutch.uils

import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 主要用于在刷新事件之间插入一个PagingData.empty()使列表显示为空
 */
class PagingEmptyValve<T : Any> {

    private val owner = Object()

    private val mLock = Mutex()

    suspend fun submitData(adapter: PagingDataAdapter<T, *>, pagingData: PagingData<T>) {
        if (mLock.holdsLock(owner)) {
            adapter.submitData(PagingData.empty())
            mLock.withLock(owner) { /*just lock*/ }
            adapter.submitData(pagingData)
        } else {
            adapter.submitData(pagingData)
        }
    }

    fun enable() {
        runBlocking {
            if (!mLock.holdsLock(owner)) {
                mLock.lock(owner)
            }
        }
    }

    //非empty的loadStates，需要处理相关事件
    fun emptyStates(loadStates: LoadStates): Boolean {
        return loadStates.isEmptyStates().apply {
            if (this) {
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