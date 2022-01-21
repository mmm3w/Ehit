package com.mitsuki.ehit.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.db.DownloadInfo
import com.mitsuki.ehit.model.entity.db.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DownloadDao {


    @Query("SELECT * FROM ${DBValue.TABLE_DOWNLOAD_INFO} ORDER BY timestamp")
    abstract fun queryDownloadData(): Flow<List<DownloadInfo>>






}