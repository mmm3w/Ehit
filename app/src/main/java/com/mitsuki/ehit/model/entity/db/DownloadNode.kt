package com.mitsuki.ehit.model.entity.db

import androidx.room.*
import com.mitsuki.ehit.const.DBValue

@Entity(tableName = DBValue.TABLE_DOWNLOAD_NODE,
    foreignKeys = [ForeignKey(
        entity = DownloadInfo::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["gid", "token"])],)
data class DownloadNode(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "page") val page: String,
    @ColumnInfo(name = "is_complete") val isComplete: Boolean,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val _id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)