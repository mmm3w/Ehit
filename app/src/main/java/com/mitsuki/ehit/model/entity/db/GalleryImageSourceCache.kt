package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.ImageSource

@Entity(
    tableName = DBValue.TABLE_IMAGE_SOURCE,
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
data class GalleryImageSourceCache(
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "page_url") val pageUrl: String,
    @ColumnInfo(name = "p_token") val pToken: String,
    @ColumnInfo(name = "left") val left: Int = -1,
    @ColumnInfo(name = "top") val top: Int = -1,
    @ColumnInfo(name = "right") val right: Int = -1,
    @ColumnInfo(name = "bottom") val bottom: Int = -1,

    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "prev_key") val prevKey: Int?,
    @ColumnInfo(name = "next_key") val nextKey: Int?,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    constructor(
        gid: Long,
        token: String,
        page: Int,
        prevKey: Int?,
        nextKey: Int?,
        source: ImageSource,
        timestamp: Long,
    ) : this(
        source.imageUrl,
        source.index,
        source.pageUrl,
        source.pToken,
        source.left,
        source.top,
        source.right,
        source.bottom,
        gid,
        token,
        page,
        prevKey,
        nextKey,
        timestamp
    )
}