package com.mitsuki.ehit.crutch.db

import android.app.Application
import androidx.room.Room
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao

object RoomData {
    private lateinit var db: AppDatabase

    fun init(application: Application) {
        db = Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "database-ehit"
            )
            .build()
    }

    val searchDao: SearchDao
        get() = db.searchDao()

    val gallery: GalleryDao
        get() = db.galleryDao()

}