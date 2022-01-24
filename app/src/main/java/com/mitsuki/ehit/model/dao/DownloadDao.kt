package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.DownloadTask
import com.mitsuki.ehit.model.entity.db.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DownloadDao {


    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO} ORDER BY timestamp")
    abstract fun queryDownloadData(): Flow<List<DownloadInfo>>


    @Transaction
    open fun updateDownloadList(task: DownloadTask) {
        //先查询出或新建task对应的缓存数据，然后更新该数据
        val info = queryDownloadInfo(task.gid, task.token)?.apply {
            total += task.total
        } ?: DownloadInfo(task)
        insertDownloadInfo(info)
        //然后将所有下载任务插入下载表
        insertDownloadNode(task.toDownloadNodeList())
    }

    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO} WHERE ${DBValue.TABLE_DOWNLOAD_INFO}.gid = :gid AND ${DBValue.TABLE_DOWNLOAD_INFO}.token = :token LIMIT 1")
    abstract fun queryDownloadInfo(gid: Long, token: String): DownloadInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadInfo(info: DownloadInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDownloadNode(nodes: List<DownloadNode>)

}