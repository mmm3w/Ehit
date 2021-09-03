package com.mitsuki.ehit.crutch.db

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.room.Room
import com.mitsuki.armory.permission.Tool
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.dev.DevRoomDB
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao
import java.io.File

object RoomData {
    private lateinit var db: AppDatabase

    @Suppress("SpellCheckingInspection")
    private const val DATABASE_NAME = "ehit"

    //仅供调试用
    private val mDevDBFile =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath

    @Suppress("SpellCheckingInspection")
    private val dbFolder = "$mDevDBFile/Ehit"
    private const val dbFile = DATABASE_NAME
    private const val dbShmFile = "$DATABASE_NAME-shm"
    private const val dbWalFile = "$DATABASE_NAME-wal"

    fun init(application: Application) {
        //直接现先在内部存储建立数据库
        if (BuildConfig.DEV &&
            Tool.Companion.checkSelfPermission(
                application, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
//            DevRoomDB.checkRoomFile(application.getExternalFilesDir("") ,
//                dbFolder, dbFile, dbShmFile, dbWalFile)
            db = Room
                .databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    "$dbFolder/$dbFile"
                )
                .build()
            return
        }
        db = Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .build()
    }

    fun rebuildDevDB(context: Context) {

    }


    val searchDao: SearchDao
        get() = db.searchDao()

    val galleryDao: GalleryDao
        get() = db.galleryDao()


}
