package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import com.mitsuki.ehit.model.entity.SimpleQuickSearch
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchDao {

    @Transaction
    open suspend fun saveQuick(name: String, key: String, meta: GalleryDataMeta.Type) {
        val count = quickCount()
        insertQuick(QuickSearch(meta, name, key, count + 1))
    }

    @Transaction
    open suspend fun mergeQuick(data: List<*>) {
        var count = quickCount()

        data.forEach {
            try {
                val node = it as Map<*, *>

                val type = GalleryDataMeta.Type.valueOf(node["type"].toString())
                val name =
                    node["name"]?.toString()?.ifEmpty { null } ?: throw IllegalArgumentException()
                val key = node["keyword"]?.toString()?.ifEmpty { null }
                    ?: throw IllegalArgumentException()

                val result = insertQuick(QuickSearch(type, name, key, ++count))
                if (result < 0) {
                    count--
                }
            } catch (e: Exception) {

            }
        }
    }

    @Transaction
    open suspend fun quickItemSwapBySort(fromSort: Int, toSort: Int) {
        val fromItemID = queryQuickIDBySort(fromSort)
        val toItemIDN = queryQuickIDBySort(toSort)
        updateQuickSort(fromItemID, toSort)
        updateQuickSort(toItemIDN, fromSort)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHistory(data: SearchHistory)

    @Query("SELECT * FROM ${DBValue.TABLE_SEARCH_HISTORY} ORDER BY created_at DESC LIMIT :count")
    abstract fun queryHistory(count: Int = 10): Flow<List<SearchHistory>>

    @Delete
    abstract suspend fun deleteHistory(data: SearchHistory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertQuick(data: QuickSearch): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateQuick(data: List<QuickSearch>)

    @Query("SELECT * FROM ${DBValue.TABLE_QUICK_SEARCH} ORDER BY sort")
    abstract suspend fun queryQuick(): List<QuickSearch>

    @Query("SELECT ${DBValue.TABLE_QUICK_SEARCH}.type,${DBValue.TABLE_QUICK_SEARCH}.name,${DBValue.TABLE_QUICK_SEARCH}.keyword FROM ${DBValue.TABLE_QUICK_SEARCH}")
    abstract suspend fun querySimpleQuick(): List<SimpleQuickSearch>

    @Query("SELECT COUNT(*) FROM ${DBValue.TABLE_QUICK_SEARCH}")
    abstract suspend fun quickCount(): Int

    @Query("SELECT * FROM ${DBValue.TABLE_QUICK_SEARCH} WHERE keyword=:key AND type=:meta")
    abstract suspend fun queryQuick(key: String, meta: GalleryDataMeta.Type): List<QuickSearch>

    @Query("DELETE FROM ${DBValue.TABLE_QUICK_SEARCH} WHERE keyword=:key AND type=:meta")
    abstract suspend fun deleteQuick(key: String, meta: GalleryDataMeta.Type)

    @Query("SELECT _id FROM ${DBValue.TABLE_QUICK_SEARCH} WHERE sort=:sort")
    abstract suspend fun queryQuickIDBySort(sort: Int): Long

    @Query("UPDATE ${DBValue.TABLE_QUICK_SEARCH} SET sort=:sort WHERE _id=:id")
    abstract suspend fun updateQuickSort(id: Long, sort: Int)
}