package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.mitsuki.ehit.const.DBValue

@Entity(
    tableName = DBValue.TABLE_GALLERY_COMMENT,
    primaryKeys = ["cid"],
    foreignKeys = [ForeignKey(
        entity = GalleryInfoCache::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["gid", "token"])],
)
class GalleryCommentCache(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "cid") val cid: Int,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)