package com.mitsuki.ehit.crutch.db

import android.content.Context
import androidx.room.Room
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.model.dao.CookieDao
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RoomData {
    private lateinit var cacheDB: CacheDatabase
    private lateinit var storeDB: StoreDatabase

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
        cacheDB = Room
            .databaseBuilder(
                context,
                CacheDatabase::class.java,
                CACHE_DB_NAME
            )
            .build()

        storeDB = Room
            .databaseBuilder(
                context,
                StoreDatabase::class.java,
                STORE_DB_NAME
            )
            .build()
    }

    val searchDao: SearchDao
        get() = storeDB.searchDao()

    val cookieDao: CookieDao
        get() = storeDB.cookieDao()

    val galleryDao: GalleryDao
        get() = cacheDB.galleryDao()

    val downloadDao: DownloadDao
        get() = cacheDB.downloadDao()

    fun storeSaveFileName(): String {
        return "ehit-${
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
        }.zip"
    }
}
