package com.mitsuki.ehit.crutch

import kotlinx.coroutines.sync.Mutex

class Blocker {
    private val mBlockLock = Mutex()
    private val mControlLock = Mutex()


    fun red() {

    }

    fun green() {

    }
}