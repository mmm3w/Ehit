package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.GalleryPreview

@Entity(
    tableName = DBValue.TABLE_GALLERY_PREVIEW,
    primaryKeys = ["index", "gid", "token"],
    foreignKeys = [ForeignKey(
        entity = GalleryInfoCache::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"]
    )],
    indices = [
        Index(value = ["gid", "token"]),
        Index(value = ["index", "gid", "token"], unique = true)
    ],
)
data class GalleryPreviewCache(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "reload_key") val reloadKey: String,
    @ColumnInfo(name = "download_url") val downloadUrl: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    constructor(gid: Long, token: String, index: Int, data: GalleryPreview)
            : this(gid, token, index, data.imageUrl, data.reloadKey, data.downloadUrl)
}