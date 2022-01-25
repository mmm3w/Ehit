package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.DownloadTask

@Entity(
    tableName = DBValue.TABLE_DOWNLOAD_INFO,
    primaryKeys = ["gid", "token"]
)
data class DownloadBaseInfo(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "thumb") val thumb: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    constructor(task: DownloadTask) : this(
        task.gid,
        task.token,
        task.thumb,
        task.title
    )
}