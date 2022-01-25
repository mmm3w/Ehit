package com.mitsuki.ehit.model.entity


data class DownloadSchedule(
    val gid: Long,
    val token: String,
    val priority: Long = System.currentTimeMillis()
) {
    private val waiting: MutableMap<String, DownloadPriority> by lazy { hashMapOf() }

    fun append(data: List<DownloadPriority>): List<DownloadPriority> {
        val patch = arrayListOf<DownloadPriority>()
        data.forEach {
            if (waiting.containsKey(it.tag)) {
                waiting[it.tag] = it
                patch.add(it)
            }
        }
        return patch
    }

    fun finish(key: String) {
        waiting.remove(key)
    }
}