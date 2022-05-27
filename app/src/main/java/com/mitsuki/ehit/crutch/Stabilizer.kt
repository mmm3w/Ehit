package com.mitsuki.ehit.crutch

import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class Stabilizer(s: Int) {
    private val lock: ReentrantLock = ReentrantLock()
    private val full: Condition = lock.newCondition()
    private var count = 0
    var size: Int = s
        set(value) {
            if (field != value) {
                val lock: ReentrantLock = this.lock
                lock.lock()
                try {
                    field = value
                    full.signal()
                } finally {
                    lock.unlock()
                }
            }
        }

    fun release() {
        val lock: ReentrantLock = this.lock
        lock.lock()
        try {
            count--
            if (count < 0) count = 0
            full.signal()
        } finally {
            lock.unlock()
        }
    }

    fun stuck() {
        val lock: ReentrantLock = this.lock
        lock.lockInterruptibly()
        try {
            while (count >= size) {
                full.await()
            }
            count++
        } finally {
            lock.unlock()
        }
    }
}