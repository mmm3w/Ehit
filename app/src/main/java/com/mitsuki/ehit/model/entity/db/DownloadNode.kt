package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue

@Entity(tableName = DBValue.TABLE_DOWNLOAD_NODE)
data class DownloadNode(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "thumb") val thumb: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "start_page") val start: Int,
    @ColumnInfo(name = "end_page") val end: Int,
    @ColumnInfo(name = "total") val total: Int,
    @ColumnInfo(name = "is_complete") val isComplete: Boolean,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val _id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)