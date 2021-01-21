package com.mitsuki.ehit.being.db

import android.app.Application
import androidx.room.Room
import com.mitsuki.ehit.core.model.dao.SearchDao

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

}