package com.mitsuki.ehit.crutch

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

class RoadGate {
    private val gate = Mutex()
    fun <T> through(action: suspend () -> T): T {
        return runBlocking {
            gate.lock()
            try {
                action()
            } finally {
                try {
                    gate.unlock()
                } catch (err: IllegalStateException) {
                }
            }
        }
    }

    fun open() {
        runBlocking {
            try {
                gate.unlock()
            } catch (err: IllegalStateException) {
//                err.printStackTrace()
            }
        }
    }

    fun close() {
        runBlocking {
            gate.tryLock()
        }
    }
}