package com.mitsuki.ehit.crutch.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mitsuki.ehit.model.dao.CookieDao
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.entity.db.*

@Database(
    entities = [
        QuickSearch::class,
        SearchHistory::class,
        CookieCache::class
    ], version = 1
)
@TypeConverters(GalleryListTypeConverter::class)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
    abstract fun cookieDao(): CookieDao
}