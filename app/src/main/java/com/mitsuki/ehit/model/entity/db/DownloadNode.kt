package com.mitsuki.ehit.model.entity.db

import android.os.Parcelable
import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = DBValue.TABLE_DOWNLOAD_NODE,
    primaryKeys = ["page", "gid", "token"],
    foreignKeys = [ForeignKey(
        entity = DownloadBaseInfo::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["gid", "token"]),
        Index(value = ["page", "gid", "token"], unique = true)
    ],
)
data class DownloadNode(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "is_complete") val isComplete: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(gid: Long, token: String, page: Int) : this(gid, token, page, true)
}