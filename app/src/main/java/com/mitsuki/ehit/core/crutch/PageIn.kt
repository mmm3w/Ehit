package com.mitsuki.ehit.core.crutch

class PageIn {
    private var mTarget: Int = 0

    private var mFlag = false

    fun jump(target: Int) {
        mFlag = true
        mTarget = target
    }

    fun replace(initPage: Int): Int {
        if (mFlag) {
            mFlag = false
            return mTarget
        }
        return initPage
    }
}