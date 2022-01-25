package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.DownloadTask
import com.mitsuki.ehit.model.entity.db.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DownloadDao {

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO} ORDER BY timestamp")
    abstract fun queryDownloadData(): Flow<List<DownloadBaseInfo>>


    @Transaction
    open fun updateDownloadList(task: DownloadTask): List<DownloadNode> {

        insertDownloadInfo(DownloadBaseInfo(task))

        val downloadList: MutableList<DownloadNode> = arrayListOf()
        (task.start..task.end).forEach {
            val isComplete = queryNodeComplete(task.gid, task.token, it) ?: false
            if (!isComplete) {
                downloadList.add(DownloadNode(task.gid, task.token, it, false))
            }
        }
        insertDownloadNode(downloadList)

        return downloadList
    }

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO} WHERE ${DBValue.TABLE_DOWNLOAD_INFO}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = :token LIMIT 1")
    abstract fun queryDownloadInfo(gid: Long, token: String): DownloadBaseInfo?

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO}")
    abstract fun queryALlDownloadInfo(): List<DownloadBaseInfo>

    @Query("SELECT is_complete FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.page = :page")
    abstract fun queryNodeComplete(gid: Long, token: String, page: Int): Boolean?

    @Query("SELECT COUNT(*) FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token")
    abstract fun queryNodeNumber(gid: Long, token: String): Int

    @Query("SELECT COUNT(*) FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.is_complete = 1")
    abstract fun queryCompletedNodeNumber(gid: Long, token: String): Int

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_NODE} WHERE ${DBValue.TABLE_DOWNLOAD_NODE}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_NODE}.token = :token AND ${DBValue.TABLE_DOWNLOAD_NODE}.is_complete = :completed")
    abstract fun queryDownloadNode(gid: Long, token: String, completed: Int = 0): List<DownloadNode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadInfo(info: DownloadBaseInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadNode(nodes: List<DownloadNode>)

}