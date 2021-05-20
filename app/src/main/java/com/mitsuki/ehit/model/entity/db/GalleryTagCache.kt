package com.mitsuki.ehit.model.entity.db

import androidx.room.*
import com.mitsuki.ehit.const.DBKey


@Entity(
    tableName = DBKey.TABLE_GALLERY_TAG,
    foreignKeys = [ForeignKey(
        entity = GalleryInfoCache::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"]
    )],
    indices = [Index(value = ["name", "group", "gid", "token"], unique = true)],
)
class GalleryTagCache(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "group") val group: String,
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "_id") val _id: Long = 0
)