package com.mitsuki.ehit.crutch.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.model.dao.CookieDao
import com.mitsuki.ehit.model.pagingsource.PagingSource
import com.mitsuki.ehit.model.pagingsource.PagingSourceImpl
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.model.repository.impl.RepositoryImpl
import com.mitsuki.ehit.ui.download.service.DownloadService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationBinds {
    @RemoteRepository
    @Singleton
    @Binds
    abstract fun getRepository(impl: RepositoryImpl): Repository

    @Binds
    abstract fun pagingSourceProvider(impl: PagingSourceImpl): PagingSource
}

@Module
@InstallIn(SingletonComponent::class)
object ApplicationProviders {

    @ApiClient
    @Singleton
    @Provides
    fun okhttpClient(): OkHttpClient {
        return HttpRookie.client
    }

    @Singleton
    @Provides
    fun cookieManager(cookieDao: CookieDao, shareData: ShareData): CookieManager {
        return CookieManager(cookieDao, shareData)
    }

    @Singleton
    @Provides
    fun shareData(@ApplicationContext context: Context): ShareData {
        return ShareData(context)
    }

    @Singleton
    @Provides
    fun notificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context) {
            channel(
                DownloadService.NOTIFICATION_CHANNEL,
                context.getString(R.string.description_download_notification_name),
                context.getString(R.string.description_download_notification),
                NotificationCompat.PRIORITY_DEFAULT
            )
        }
    }
}