package com.mitsuki.ehit.model.download

import android.util.Log
import androidx.collection.ArraySet
import androidx.collection.arraySetOf
import com.mitsuki.ehit.model.entity.db.DownloadNode
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class DownloadQueue(private var maxCount: Int) {

    private var currentTask: String? = null

    private val tagList: MutableSet<String> = arraySetOf()
    private val nodeMap: MutableMap<String, MutableSet<DownloadNode>> = hashMapOf()

    private val lock: ReentrantLock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val overDelivery = lock.newCondition()

    private var count = AtomicInteger(0)

    fun put(tag: String, data: List<DownloadNode>) {
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

    fun put(data: List<Pair<String, List<DownloadNode>>>) {
        val lock = this.lock
        lock.lockInterruptibly()
        try {
            data.forEach {
                if (currentTask == it.first) {
                    nodeMap[currentTask]?.addAll(it.second)
                } else {
                    tagList.add(it.first)
                    nodeMap[it.first] = ArraySet(it.second)
                }
            }
            notEmpty.signal()
        } finally {
            lock.unlock()
        }
    }

    fun take(): DownloadNode {
        val lock = this.lock
        val count: AtomicInteger = count
        val c: Int
        lock.lockInterruptibly()
        try {

            while (count.get() >= maxCount) {
                overDelivery.await()
            }

            var data: DownloadNode?
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
        val c: Int
        lock.lockInterruptibly()
        try {
            c = count.getAndDecrement()
            if (c - 1 < maxCount) overDelivery.signal()


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

    private fun obtain(c: AtomicInteger): DownloadNode? {
        if (currentTask == null) {
            currentTask = tagList.firstOrNull()
        }
        while (currentTask != null) {
            val data =
                nodeMap[currentTask]?.firstOrNull()?.apply { nodeMap[currentTask]?.remove(this) }
            if (data != null) {
                return data
            } else {
                if (c.get() > 0) return null
                nodeMap.remove(currentTask)
                currentTask = tagList.firstOrNull()
            }
        }
        return null
    }
}