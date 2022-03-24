package com.mitsuki.ehit.ui.download.service

import android.app.Service
import com.mitsuki.armory.base.NotificationHelper
import javax.inject.Inject

class DownloadNotify @Inject constructor(private val helper: NotificationHelper) {

    companion object {
        const val NOTIFICATION_ID = 10002
        const val NOTIFICATION_CHANNEL = "DOWNLOAD"
    }

    private var isNeedRecall = true

    /**
     *         val intent = Intent(this, MainActivity::class.java)
    val pi = PendingIntent.getActivity(this, 0, intent, 0)
     */

    fun replyForeground(service: Service) {
        if (isNeedRecall) {
            isNeedRecall = false

            helper.startForeground(
                service,
                NOTIFICATION_CHANNEL,
                NOTIFICATION_ID
            ) {
                it.setSmallIcon(android.R.drawable.stat_sys_download)
                it.setContentTitle("开始下载")
            }
        }
    }

    fun notifyFinish(service: Service) {
        helper.notify(service, NOTIFICATION_CHANNEL, NOTIFICATION_ID) {
            it.setSmallIcon(android.R.drawable.stat_sys_download)
            it.setAutoCancel(true)
            it.setContentTitle("下载完成")
        }
    }

    fun notifyUpdate(service: Service, title: String, content: String) {
        helper.notify(service, NOTIFICATION_CHANNEL, NOTIFICATION_ID) {
            it.setSmallIcon(android.R.drawable.stat_sys_download)
            it.setContentTitle(title)
            it.setContentText(content)
        }
    }

    fun reset() {
        isNeedRecall = true
    }
}