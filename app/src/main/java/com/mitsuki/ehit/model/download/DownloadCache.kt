package com.mitsuki.ehit.model.download

import com.mitsuki.ehit.model.entity.DownloadPriority
import com.mitsuki.ehit.model.entity.DownloadSchedule
import com.mitsuki.ehit.model.entity.DownloadTask
import com.mitsuki.ehit.model.entity.db.DownloadNode

class DownloadCache {

    private val sPoolSync = Any()

    private val schedule: MutableMap<String, DownloadSchedule> by lazy { hashMapOf() }

    fun append(data: DownloadTask, newNode: List<DownloadNode>): List<DownloadPriority> {
        synchronized(sPoolSync) {
            return (schedule[data.key] ?: DownloadSchedule(data.gid, data.token).also { node ->
                schedule[data.key] = node
            }).let { node ->
                newNode.map { DownloadPriority(it.gid, it.token, it.page, node.priority) }
                    .apply { node.append(this) } //最后将差分任务投入线程池
            }
        }
    }

    fun clear() {
        synchronized(sPoolSync) {
            schedule.clear()
        }
    }
}