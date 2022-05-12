package com.mitsuki.ehit.crutch.di

import android.content.Context
import androidx.core.app.NotificationCompat
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.ehit.R
import com.mitsuki.ehit.service.download.DownloadNotify
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ToolsProviders {

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