package com.mitsuki.ehit.being.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mitsuki.ehit.core.model.dao.SearchDao
import com.mitsuki.ehit.core.model.entity.QuickSearch
import com.mitsuki.ehit.core.model.entity.SearchHistory

@Database(entities = [SearchHistory::class, QuickSearch::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
}