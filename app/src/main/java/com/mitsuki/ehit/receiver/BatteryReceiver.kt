package com.mitsuki.ehit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post

class BatteryReceiver : BroadcastReceiver(), EventEmitter {

    companion object {
        fun intentFilter(): IntentFilter {
            return IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
            }
        }

        const val BATTERY_LEVEL_CHARGING = -1
        const val BATTERY_LEVEL_0 = 0
        const val BATTERY_LEVEL_1 = 1
        const val BATTERY_LEVEL_2 = 2
        const val BATTERY_LEVEL_3 = 3
        const val BATTERY_LEVEL_4 = 4
        const val BATTERY_LEVEL_5 = 5
        const val BATTERY_LEVEL_6 = 6
        const val BATTERY_LEVEL_7 = 7
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            postIntentData(intent)
        }
    }

    fun postIntentData(intent: Intent) {
        val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        if (isCharging) {
            post("battery", BATTERY_LEVEL_CHARGING)
        } else {
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val b = level * 100 / scale.toFloat()
            when {
                b in 90f..100F -> post("battery", BATTERY_LEVEL_7)
                b in 80f..90f -> post("battery", BATTERY_LEVEL_6)
                b in 65f..80f -> post("battery", BATTERY_LEVEL_5)
                b in 50f..65f -> post("battery", BATTERY_LEVEL_4)
                b in 35f..50f -> post("battery", BATTERY_LEVEL_3)
                b in 15f..35f -> post("battery", BATTERY_LEVEL_2)
                b in 5f..15f -> post("battery", BATTERY_LEVEL_1)
                b <= 5F -> post("battery", BATTERY_LEVEL_0)
            }
        }
    }

    override val eventEmitter: Emitter = Emitter()
}
