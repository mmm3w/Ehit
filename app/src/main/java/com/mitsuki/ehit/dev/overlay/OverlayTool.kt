package com.mitsuki.ehit.dev.overlay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mitsuki.armory.systemoverlay.*
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.dev.DevEnv

object OverlayTool : LifecycleObserver {

    private lateinit var triggerButton: OverlayView
    private lateinit var controlPanel: OverlayView
    private lateinit var actionReference: (Int) -> Unit

    fun init(context: Context) {
        if (BuildConfig.DEV) {
            OverlayManager.init(context)

            triggerButton = SideOverlay(context).apply {
                layout(R.layout.overlay_btn)
                setOnClickListener {
                    OverlayManager.switch(controlPanel)
                }
            }

            controlPanel = PanelOverlay(context).apply {
                setOnClickListener(OverlayTool::onPanelControl)
                findViewById<TextView>(R.id.dev_overlay_db_import)?.setOnClickListener(OverlayTool::onPanelControl)
                findViewById<TextView>(R.id.dev_overlay_db_export)?.setOnClickListener(OverlayTool::onPanelControl)
                findViewById<TextView>(R.id.dev_overlay_nsfw)?.setOnClickListener(OverlayTool::onPanelControl)
            }
        }
    }

    fun permission(activity: Activity): Intent? {
        if (!BuildConfig.DEV) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
            }
        }
        return null
    }

    fun panelAction(action: (Int) -> Unit) {
        actionReference = action
    }

    private fun onPanelControl(view: View) {
        when (view.id) {
            R.id.dev_overlay_db_import,
            R.id.dev_overlay_db_export -> actionReference.invoke(view.id)
            R.id.dev_overlay_nsfw ->
                AppHolder.toast(if (DevEnv.nsfwSwitch()) "已开启NSFW" else "已关闭NSFW")
        }

        OverlayManager.switch(triggerButton)
    }

    val triggerX: Int get() = triggerButton.layoutParams().x
    val triggerY: Int get() = triggerButton.layoutParams().y

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun showOverlay() {
        if (BuildConfig.DEV) {
            try {
                OverlayManager.switch(triggerButton)
            } catch (inner: Throwable) {
                inner.printStackTrace()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun hideOverlay() {
        if (BuildConfig.DEV) {
            try {
                OverlayManager.switch(null)
            } catch (inner: Throwable) {
                inner.printStackTrace()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun exit() {
        if (BuildConfig.DEV) {
            try {
                OverlayManager.exit()
            } catch (inner: Throwable) {
                inner.printStackTrace()
            }
        }
    }
}