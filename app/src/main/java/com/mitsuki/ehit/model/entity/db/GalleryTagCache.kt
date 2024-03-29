package com.mitsuki.ehit.model.entity.db

import androidx.room.*
import com.mitsuki.ehit.const.DBValue


@Entity(
    tableName = DBValue.TABLE_GALLERY_TAG,
    foreignKeys = [ForeignKey(
        entity = GalleryInfoCache::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["gid", "token"]),
        Index(value = ["name", "group", "gid", "token"], unique = true)
    ],
)
class GalleryTagCache(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "group") val group: String,
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val _id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)