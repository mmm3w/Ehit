package com.mitsuki.ehit.crutch.uils

import kotlinx.coroutines.sync.Mutex


fun Mutex.tryUnlock(owner: Any? = null) {
    try {
        unlock(owner)
    } catch (err: IllegalStateException) {

    }
}