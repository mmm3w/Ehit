package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.entity.DownloadNotifyProgress
import com.mitsuki.ehit.model.entity.db.DownloadBaseInfo
import com.mitsuki.ehit.model.entity.db.DownloadNode
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DownloadDao {

    @Transaction
    open fun updateDownloadList(message: DownloadMessage): List<DownloadNode> {
        val cache = queryDownloadInfo(message.gid, message.token)
        if (cache == null) {
            insertDownloadInfo(DownloadBaseInfo(message))
        } else {
            updateDownloadInfo(DownloadBaseInfo(message))
        }

        val downloadList: MutableList<DownloadNode> = arrayListOf()
        (message.start..message.end).forEach { index ->
            val isComplete = queryNodeComplete(message.gid, message.token, index) ?: 0
            if (isComplete == 0) {
                insertDownloadNode(DownloadNode(message.gid, message.token, index).apply {
                    downloadList.add(this)
                })
            }
        }
        return downloadList
    }

    @Query("SELECT ${DBValue.TABLE_DOWNLOAD_INFO}.title, COUNT(${DBValue.TABLE_DOWNLOAD_NODE}.timestamp) AS total, COUNT(CASE WHEN ${DBValue.TABLE_DOWNLOAD_NODE}.download_state=0 THEN NULL ELSE 1 END) AS completed FROM ${DBValue.TABLE_DOWNLOAD_NODE} LEFT JOIN ${DBValue.TABLE_DOWNLOAD_INFO} ON ${DBValue.TABLE_DOWNLOAD_INFO}.gid = ${DBValue.TABLE_DOWNLOAD_NODE}.gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = ${DBValue.TABLE_DOWNLOAD_NODE}.token WHERE ${DBValue.TABLE_DOWNLOAD_INFO}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = :token")
    abstract fun galleryDownloadProgress(gid: Long, token: String): Flow<DownloadNotifyProgress>

    @Query("SELECT ${DBValue.TABLE_DOWNLOAD_NODE}.gid, ${DBValue.TABLE_DOWNLOAD_NODE}.token, ${DBValue.TABLE_DOWNLOAD_INFO}.thumb, ${DBValue.TABLE_DOWNLOAD_INFO}.local_thumb, ${DBValue.TABLE_DOWNLOAD_INFO}.title, COUNT(${DBValue.TABLE_DOWNLOAD_NODE}.timestamp) AS total, COUNT(CASE WHEN ${DBValue.TABLE_DOWNLOAD_NODE}.download_state=1 THEN 1 ELSE NULL END) AS completed, COUNT(CASE WHEN ${DBValue.TABLE_DOWNLOAD_NODE}.download_state=2 THEN 1 ELSE NULL END) AS failed FROM ${DBValue.TABLE_DOWNLOAD_NODE} LEFT JOIN ${DBValue.TABLE_DOWNLOAD_INFO} ON ${DBValue.TABLE_DOWNLOAD_INFO}.gid = ${DBValue.TABLE_DOWNLOAD_NODE}.gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = ${DBValue.TABLE_DOWNLOAD_NODE}.token GROUP BY ${DBValue.TABLE_DOWNLOAD_NODE}.gid,${DBValue.TABLE_DOWNLOAD_NODE}.token ORDER BY ${DBValue.TABLE_DOWNLOAD_INFO}.timestamp")
    abstract fun queryDownloadList(): Flow<List<DownloadListInfo>>

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO} WHERE ${DBValue.TABLE_DOWNLOAD_INFO}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = :token LIMIT 1")
    abstract fun queryDownloadInfo(gid: Long, token: String): DownloadBaseInfo?

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO}")
    abstract fun queryALlDownloadInfo(): List<DownloadBaseInfo>

    @Query("SELECT download_state FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.page = :page")
    abstract fun queryNodeComplete(gid: Long, token: String, page: Int): Int?

    @Query("SELECT COUNT(*) FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token")
    abstract fun queryNodeNumber(gid: Long, token: String): Int

    @Query("SELECT COUNT(*) FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.download_state = 1")
    abstract fun queryCompletedNodeNumber(gid: Long, token: String): Int

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token")
    abstract fun queryDownloadNode(gid: Long, token: String): List<DownloadNode>

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.download_state = :state")
    abstract fun queryDownloadNodeWithState(
        gid: Long,
        token: String,
        state: Int = 0
    ): List<DownloadNode>

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND NOT ${DBValue.TABLE_DOWNLOAD_NODE}.download_state = :state")
    abstract fun queryDownloadNodeWithNotState(
        gid: Long,
        token: String,
        state: Int = 0
    ): List<DownloadNode>

    @Query("DELETE FROM ${DBValue.TABLE_DOWNLOAD_INFO} WHERE ${DBValue.TABLE_DOWNLOAD_INFO}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = :token")
    abstract fun deleteDownloadInfo(gid: Long, token: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDownloadInfo(info: DownloadBaseInfo)

    @Update
    abstract fun updateDownloadInfo(info: DownloadBaseInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadNode(node: DownloadNode)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateDownloadNode(node: DownloadNode)
}