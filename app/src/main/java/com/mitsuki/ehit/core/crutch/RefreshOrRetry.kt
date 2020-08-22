package com.mitsuki.ehit.core.crutch

import androidx.paging.PagingDataAdapter

//暂时没有更好的处理方式
//临时用这种方式顶一下
class RefreshOrRetry {
    var mFlag = false

    fun mark() {
        mFlag = true
    }

    fun trigger(adapter: PagingDataAdapter<*, *>) {
        if (mFlag) {
            mFlag = false
            adapter.retry()
        } else {
            adapter.refresh()
        }
    }

}