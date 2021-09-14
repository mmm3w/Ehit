package com.mitsuki.ehit.crutch.db

import android.content.Context
import androidx.room.Room
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RoomData {
    private lateinit var db: AppDatabase

    private const val CACHE_DB_NAME = "ehit-cache"
    private const val STORE_DB_NAME = "ehit"

    val storeFileArray
        get() = arrayOf(
            STORE_DB_NAME,
            "${STORE_DB_NAME}-shm",
            "${STORE_DB_NAME}-wal"
        )

    val dbFolder get() = File(AppHolder.cacheDir.parent, "databases")

    fun init(context: Context) {
        db = Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                STORE_DB_NAME
            )
            .build()
    }

    val searchDao: SearchDao
        get() = db.searchDao()

    val galleryDao: GalleryDao
        get() = db.galleryDao()

    fun close() {
        db.close()
    }

    fun rebuild(context: Context) {
        init(context)
    }

    fun storeSaveFileName(): String {
        return "ehit-${
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
        }.zip"
    }
}
