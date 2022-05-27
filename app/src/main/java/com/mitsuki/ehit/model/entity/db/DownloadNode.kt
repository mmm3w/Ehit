package com.mitsuki.ehit.model.entity.db

import android.os.Parcelable
import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import kotlinx.parcelize.Parcelize

/**
 * 单个下载节点的信息
 */
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
    @ColumnInfo(name = "download_state") val downloadState: Int = 0, //0未开始，1完成，2异常
    @ColumnInfo(name = "local_path") val localPath: String = "",
    @ColumnInfo(name = "content_length") val fileSize: Int = -1,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) : Parcelable {

    fun isSameNode(other: DownloadNode): Boolean {
        return gid == other.gid &&
                token == other.token &&
                page == other.page
    }

    override fun equals(other: Any?): Boolean {
        return other is DownloadNode &&
                gid == other.gid &&
                token == other.token &&
                page == other.page
    }

    override fun hashCode(): Int {
        var result = gid.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + page
        return result
    }
}