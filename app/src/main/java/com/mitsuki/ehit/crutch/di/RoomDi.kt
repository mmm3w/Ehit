package com.mitsuki.ehit.crutch.di

import android.content.Context
import androidx.room.Room
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.crutch.db.CacheDatabase
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.crutch.db.StoreDatabase
import com.mitsuki.ehit.model.dao.CookieDao
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.dao.SearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDi {
    @Singleton
    @Provides
    fun cacheDB(@ApplicationContext context: Context): CacheDatabase {
        return Room
            .databaseBuilder(
                context,
                CacheDatabase::class.java,
                RoomData.CACHE_DB_NAME
            )
            .build()
    }

    @Singleton
    @Provides
    fun storeDB(@ApplicationContext context: Context): StoreDatabase {
        return Room
            .databaseBuilder(
                context,
                StoreDatabase::class.java,
                RoomData.STORE_DB_NAME
            )
            .build()
    }

    @Provides
    fun searchDao(db: StoreDatabase): SearchDao {
        return db.searchDao()
    }

    @Provides
    fun cookieDao(db: StoreDatabase): CookieDao {
        return db.cookieDao()
    }

    @Provides
    fun galleryDao(db: CacheDatabase): GalleryDao {
        return db.galleryDao()
    }

    @Provides
    fun downloadDao(db: CacheDatabase): DownloadDao {
        return db.downloadDao()
    }
}