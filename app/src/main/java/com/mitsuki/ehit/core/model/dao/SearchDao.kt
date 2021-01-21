package com.mitsuki.ehit.core.model.dao

import androidx.room.*
import com.mitsuki.ehit.core.model.entity.QuickSearch
import com.mitsuki.ehit.core.model.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(data: SearchHistory)

    @Query("SELECT * FROM search_history ORDER BY created_at DESC LIMIT :count")
    fun queryHistory(count: Int = 10): Flow<List<SearchHistory>>

    @Delete
    suspend fun deleteHistory(data: SearchHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuick(data: QuickSearch)

    @Query("SELECT * FROM quick_search ORDER BY created_at DESC")
    fun queryQuick(): Flow<List<QuickSearch>>

    @Delete
    suspend fun deleteQuick(data: QuickSearch)

}