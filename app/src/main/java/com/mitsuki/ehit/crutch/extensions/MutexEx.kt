package com.mitsuki.ehit.crutch.extensions

import kotlinx.coroutines.sync.Mutex

fun Mutex.tryUnlock(owner: Any? = null) {
    try {
        unlock(owner)
    } catch (err: Exception) {

    }
}

suspend fun Mutex.justLock(owner: Any? = null){
    lock(owner)
    tryUnlock(owner)
}