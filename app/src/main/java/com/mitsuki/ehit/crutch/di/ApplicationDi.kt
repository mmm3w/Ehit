package com.mitsuki.ehit.crutch.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.ShareData
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.network.FakeHeader
import com.mitsuki.ehit.model.dao.CookieDao
import com.mitsuki.ehit.model.pagingsource.PagingSource
import com.mitsuki.ehit.model.pagingsource.PagingSourceImpl
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.model.repository.impl.RepositoryImpl
import com.mitsuki.ehit.ui.download.service.DownloadNotify
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
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

    @Singleton
    @Binds
    abstract fun cookieJar(impl: CookieJarImpl): CookieJar
}

@Module
@InstallIn(SingletonComponent::class)
object ApplicationProviders {

    @ApiClient
    @Singleton
    @Provides
    fun apiClient(cookieJar: CookieJar): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(FakeHeader())
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
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
                DownloadNotify.NOTIFICATION_CHANNEL,
                context.getString(R.string.description_download_notification_name),
                context.getString(R.string.description_download_notification),
                NotificationCompat.PRIORITY_LOW
            )
        }
    }
}