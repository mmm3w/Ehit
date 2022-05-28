package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.DownloadMessage

/**
 * 下载的一些基础信息
 */
@Entity(
    tableName = DBValue.TABLE_DOWNLOAD_INFO,
    primaryKeys = ["gid", "token"]
)
data class DownloadBaseInfo(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "thumb") val thumb: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "local_thumb") var localThumb: String = ""
) {
    constructor(message: DownloadMessage) : this(
        message.gid,
        message.token,
        message.thumb,
        message.title
    )

    override fun equals(other: Any?): Boolean {
        return other is DownloadBaseInfo &&
                other.gid == gid &&
                other.token == token &&
                other.thumb == thumb &&
                other.title == title &&
                other.localThumb == localThumb &&
                other.timestamp == timestamp
    }

    override fun hashCode(): Int {
        var result = gid.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + thumb.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + localThumb.hashCode()
        return result
    }
}