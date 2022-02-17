package com.mitsuki.ehit.model.download

import androidx.collection.ArraySet
import androidx.collection.arraySetOf
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class DownloadQueue<T>(private var maxCount: Int) {

    private var currentTask: String? = null

    private val tagList: MutableSet<String> = arraySetOf()
    private val nodeMap: MutableMap<String, MutableSet<T>> = hashMapOf()

    private val lock: ReentrantLock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val overDelivery = lock.newCondition()

    private var count = AtomicInteger(0)

    fun put(tag: String, data: List<T>) {
        if (data.isEmpty()) return
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            if (currentTask == tag) {
                nodeMap[currentTask]?.addAll(data)
            } else {
                tagList.add(tag)
                nodeMap[tag] = ArraySet(data)
            }
            notEmpty.signal()
        } finally {
            lock.unlock()
        }
    }

    fun put(data: List<Pair<String, List<T>>>) {
        if (data.isEmpty()) return
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            data.forEach {
                if (it.second.isNotEmpty()) {
                    if (currentTask == it.first) {
                        nodeMap[currentTask]?.addAll(it.second)
                    } else {
                        tagList.add(it.first)
                        nodeMap[it.first] = ArraySet(it.second)
                    }
                }
            }
            notEmpty.signal()
        } finally {
            lock.unlock()
        }
    }

    fun take(): T {
        val lock = this.lock
        val count: AtomicInteger = count
        val c: Int
        lock.lockInterruptibly()
        try {
            while (count.get() >= maxCount) {
                overDelivery.await()
            }

            var data: T?
            while (obtain(count).apply { data = this } == null) {
                notEmpty.await()
            }

            c = count.getAndIncrement()
            if (c + 1 < maxCount) overDelivery.signal()
            return data ?: throw IllegalAccessError()
        } finally {
            lock.unlock()
        }
    }

    fun cancel(tag: String) {
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            if (currentTask == tag) currentTask = null
            tagList.remove(tag)
            nodeMap.remove(tag)
        } finally {
            lock.unlock()
        }
    }

    fun clear() {
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            currentTask = null
            tagList.clear()
            nodeMap.clear()
        } finally {
            lock.unlock()
        }
    }

    fun idle() {
        val lock = this.lock
        val count: AtomicInteger = count
        if (count.get() <= 0) return
        val c: Int
        lock.lockInterruptibly()
        try {
            c = count.getAndDecrement()
            if (c - 1 < maxCount) overDelivery.signal()

            if (isTargetEmpty(currentTask)) {
                currentTask = null
                nodeMap.remove(currentTask)
                notEmpty.signal()
            }
        } finally {
            lock.unlock()
        }
    }

    fun setMaxCount(c: Int) {
        val lock = this.lock
        val count: AtomicInteger = count
        lock.lockInterruptibly()
        try {
            maxCount = c
            if (count.get() < maxCount)
                overDelivery.signal()
        } finally {
            lock.unlock()
        }
    }

    private fun isTargetEmpty(target: String?): Boolean {
        if (target == null) return true
        return nodeMap[target]?.isEmpty() ?: true
    }

    private fun obtain(c: AtomicInteger): T? {
        if (currentTask == null && c.get() <= 0) {
            currentTask = tagList.firstOrNull()?.apply { tagList.remove(this) }
        }
        while (currentTask != null) {
            val data =
                nodeMap[currentTask]?.firstOrNull()?.apply { nodeMap[currentTask]?.remove(this) }
            if (data != null) {
                return data
            } else {
                if (c.get() > 0) return null
                nodeMap.remove(currentTask)
                currentTask = tagList.firstOrNull()?.apply { tagList.remove(this) }
            }
        }
        return null
    }
}