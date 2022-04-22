package com.mitsuki.ehit.service.export

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

/**
 * 导出数据服务，不需要前台服务
 *
 */
@AndroidEntryPoint
class ExportService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_STICKY
    }
}