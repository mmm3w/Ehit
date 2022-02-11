package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.model.entity.DownloadMessage
import com.mitsuki.ehit.model.entity.db.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DownloadDao {

    @Transaction
    open fun updateDownloadList(message: DownloadMessage): List<DownloadNode> {

        insertDownloadInfo(DownloadBaseInfo(message))

        val downloadList: MutableList<DownloadNode> = arrayListOf()
        (message.start..message.end).forEach {
            val isComplete = queryNodeComplete(message.gid, message.token, it) ?: 0
            if (isComplete == 0) {
                downloadList.add(DownloadNode(message.gid, message.token, it))
            }
        }
        insertDownloadNode(downloadList)

        return downloadList
    }

    @Query("SELECT ${DBValue.TABLE_DOWNLOAD_NODE}.gid, ${DBValue.TABLE_DOWNLOAD_NODE}.token, ${DBValue.TABLE_DOWNLOAD_INFO}.thumb, ${DBValue.TABLE_DOWNLOAD_INFO}.title, COUNT(${DBValue.TABLE_DOWNLOAD_NODE}.timestamp) AS total, COUNT(CASE WHEN ${DBValue.TABLE_DOWNLOAD_NODE}.download_state=1 THEN 1 ELSE NULL END) AS completed FROM ${DBValue.TABLE_DOWNLOAD_NODE} LEFT JOIN ${DBValue.TABLE_DOWNLOAD_INFO} ON ${DBValue.TABLE_DOWNLOAD_INFO}.gid = ${DBValue.TABLE_DOWNLOAD_NODE}.gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = ${DBValue.TABLE_DOWNLOAD_NODE}.token GROUP BY ${DBValue.TABLE_DOWNLOAD_NODE}.gid,${DBValue.TABLE_DOWNLOAD_NODE}.token ORDER BY ${DBValue.TABLE_DOWNLOAD_INFO}.timestamp")
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

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.download_state = :completed")
    abstract fun queryDownloadNode(gid: Long, token: String, completed: Int = 0): List<DownloadNode>

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadInfo(info: DownloadBaseInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadNode(nodes: List<DownloadNode>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateDownloadNode(node: DownloadNode)
}