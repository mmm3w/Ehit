package com.mitsuki.ehit.crutch.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao
import com.mitsuki.ehit.model.entity.db.*

@Database(
    entities = [
        GalleryCommentCache::class,
        GalleryImageSourceCache::class,
        GalleryInfoCache::class,
        GalleryPreviewCache::class,
        GalleryTagCache::class,
        DownloadNode::class,
        DownloadInfo::class,
    ], version = 1
)
@TypeConverters(GalleryListTypeConverter::class)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun galleryDao(): GalleryDao

    abstract fun downloadDao(): DownloadDao
}