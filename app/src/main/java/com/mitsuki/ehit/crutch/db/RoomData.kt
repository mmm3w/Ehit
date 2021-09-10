package com.mitsuki.ehit.crutch.db

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
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

    fun init(context: Context) {
        db = Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .build()
    }

    fun importAndRebuild(context: Context) {
        db.close()
        import()
        init(context)
    }

    fun exportAndBuild(context: Context) {
        db.close()
        export()
        init(context)
    }

    private fun import() {

    }

    private fun export() {

    }

    val searchDao: SearchDao
        get() = db.searchDao()

    val galleryDao: GalleryDao
        get() = db.galleryDao()


}
