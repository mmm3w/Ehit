package com.mitsuki.ehit.model.dao

import androidx.paging.PagingData
import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.db.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(data: SearchHistory)

    @Query("SELECT * FROM ${DBValue.TABLE_SEARCH_HISTORY} ORDER BY created_at DESC LIMIT :count")
    fun queryHistory(count: Int = 10): Flow<List<SearchHistory>>

    @Delete
    suspend fun deleteHistory(data: SearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuick(data: QuickSearch)

    @Query("SELECT * FROM ${DBValue.TABLE_QUICK_SEARCH} ORDER BY created_at DESC")
    fun queryQuick(): Flow<List<QuickSearch>>

    @Delete
    suspend fun deleteQuick(data: QuickSearch)

}