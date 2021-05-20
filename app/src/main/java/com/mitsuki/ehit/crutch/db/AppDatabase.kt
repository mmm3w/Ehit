package com.mitsuki.ehit.crutch.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.entity.db.*

@Database(
    entities = [
        SearchHistory::class,
        QuickSearch::class,
        GalleryInfoCache::class,
        GalleryTagCache::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao

    abstract fun galleryDao(): GalleryDao
}