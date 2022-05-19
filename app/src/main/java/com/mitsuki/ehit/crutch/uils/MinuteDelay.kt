package com.mitsuki.ehit.crutch.uils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post

class MinuteDelay(owner: LifecycleOwner) : Runnable, EventEmitter, DefaultLifecycleObserver {
    init {
        owner.lifecycle.addObserver(this)
    }

    private var isStarted = false
    private val handler = Handler(Looper.getMainLooper())
    override val eventEmitter: Emitter = Emitter()

    fun start() {
        if (isStarted) return
        isStarted = true
        handler.post(this)
    }

    fun stop() {
        isStarted = false
    }

    override fun run() {
        if (!isStarted) return
        val time = System.currentTimeMillis()
        val delay = 60000 - time % 60000
        handler.postDelayed(this, delay)
        post("timestamp", time)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stop()
    }
}