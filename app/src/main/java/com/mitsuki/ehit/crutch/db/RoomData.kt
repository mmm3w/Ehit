package com.mitsuki.ehit.crutch.db

import android.Manifest
import android.app.Application
import android.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.room.Room
import com.mitsuki.armory.PermissionTool
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao

object RoomData {
    private lateinit var db: AppDatabase

    fun init(application: Application) {

        if (BuildConfig.DEV) {

            if (PermissionTool.checkSelfPermission(
                    application,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            ) {
                //创建外部数据库

                return
            }
        }
        db = Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "database-ehit"
            )
            .build()
    }

    fun rebuildDevDB() {

    }


    val searchDao: SearchDao
        get() = db.searchDao()

    val galleryDao: GalleryDao
        get() = db.galleryDao()


}
