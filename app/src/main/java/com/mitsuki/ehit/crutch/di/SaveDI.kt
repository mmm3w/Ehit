package com.mitsuki.ehit.crutch.di

import android.content.Context
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.crutch.save.ShareData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SaveProviders {
    @Singleton
    @Provides
    fun shareData(@ApplicationContext context: Context): ShareData {
        return ShareData(context)
    }

    @Singleton
    @Provides
    fun memoryData(shareData: ShareData): MemoryData {
        return MemoryData(shareData)
    }
}