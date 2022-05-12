package com.mitsuki.ehit.crutch.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.pagingsource.PagingSource
import com.mitsuki.ehit.model.pagingsource.PagingSourceImpl
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.model.repository.impl.RepositoryImpl
import com.mitsuki.ehit.service.download.DownloadNotify
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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