package com.mitsuki.ehit.ui.download.service

import android.app.Service
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mitsuki.armory.base.NotificationHelper
import com.mitsuki.ehit.R
import javax.inject.Inject

class DownloadNotify @Inject constructor(private val helper: NotificationHelper) {

    companion object {
        const val NOTIFICATION_ID = 10002
        const val NOTIFICATION_CHANNEL = "DOWNLOAD"
    }


    /**
     *         val intent = Intent(this, MainActivity::class.java)
    val pi = PendingIntent.getActivity(this, 0, intent, 0)
     */

    fun replyForeground(service: Service) {
        helper.startForeground(
            service,
            NOTIFICATION_CHANNEL,
            NOTIFICATION_ID
        ) {
            it.setSmallIcon(android.R.drawable.stat_sys_download)
            it.setContentTitle(service.getText(R.string.text_start_download))
            it.setCategory(NotificationCompat.CATEGORY_PROGRESS)
            it.setProgress(0, 0, true)
        }
    }

    fun notifyFinish(service: Service) {
        helper.notify(service, NOTIFICATION_CHANNEL, NOTIFICATION_ID) {
            it.setSmallIcon(android.R.drawable.stat_sys_download_done)
            it.setAutoCancel(true)
            it.setContentTitle(service.getText(R.string.text_download_finish))
        }
    }

    fun notifyUpdate(service: Service, title: String, total: Int, over: Int) {
        helper.notify(service, NOTIFICATION_CHANNEL, NOTIFICATION_ID) {
            it.setSmallIcon(android.R.drawable.stat_sys_download)
            it.setContentTitle(title)
            it.setCategory(NotificationCompat.CATEGORY_PROGRESS)
            it.setProgress(total, over, false)
        }
    }
}