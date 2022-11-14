package com.mitsuki.ehit.crutch.save

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.const.Setting
import java.lang.IllegalArgumentException

class ShareData(context: Context) {
    private val mDefaultSP: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val INNER_APP_VERSION = "INNER_APP_VERSION"

        const val SP_SECURITY = "SP_SECURITY"
        const val SP_INITIAL_INSTALLATION = "SP_INITIAL_INSTALLATION"

        const val SP_DOMAIN = "SP_DOMAIN"
        const val SP_SHOW_JP_TITLE = "SP_SHOW_JP_TITLE"
        const val SP_SHOW_PAGE = "SP_SHOW_PAGE"
        const val SP_DISABLE_SCREENSHOTS = "SP_DISABLE_SCREENSHOTS"
        const val SP_DOWNLOAD_THREAD = "SP_DOWNLOAD_THREAD"
        const val SP_DOWNLOAD_ORIGINAL = "SP_DOWNLOAD_ORIGINAL"
        const val SP_PRELOAD_IMAGE = "SP_PRELOAD_IMAGE"
        const val SP_PROXY_MODE = "SP_PROXY_MODE"
        const val SP_PROXY_IP = "SP_PROXY_IP"
        const val SP_PROXY_PORT = "SP_PROXY_PORT"
        const val SP_THEME = "SP_THEME"

        const val SP_GALLERY_TOUCH_HOTSPOT_TIPS = "SP_GALLERY_TOUCH_HOTSPOT_TIPS"

        //read setting
        const val SP_SCREEN_ORIENTATION = "SP_SCREEN_ORIENTATION"
        const val SP_READING_DIRECTION = "SP_READING_DIRECTION"
        const val SP_IMAGE_ZOOM = "SP_IMAGE_ZOOM"
        const val SP_KEEP_BRIGHT = "SP_KEEP_BRIGHT"
        const val SP_SHOW_TIME = "SP_SHOW_TIME"
        const val SP_SHOW_BATTERY = "SP_SHOW_BATTERY"
        const val SP_SHOW_PROGRESS = "SP_SHOW_PROGRESS"
        const val SP_SHOW_PAGE_PADDING = "SP_SHOW_PAGE_PADDING"
        const val SP_VOLUME_BUTTON_TURN_PAGES = "SP_VOLUME_BUTTON_TURN_PAGES"
        const val SP_FULL_SCREEN = "SP_FULL_SCREEN"
        const val SP_CUSTOM_BRIGHTNESS = "SP_CUSTOM_BRIGHTNESS"

    }

    fun string(key: String, default: String = ""): String =
        mDefaultSP.getString(key, default) ?: default

    fun boolean(key: String, default: Boolean = false): Boolean =
        mDefaultSP.getBoolean(key, default)

    fun int(key: String, default: Int = 0): Int =
        mDefaultSP.getInt(key, default)

    fun float(key: String, default: Float = 0F): Float =
        mDefaultSP.getFloat(key, default)

    fun edit(func: SharedPreferences.Editor.() -> Unit) {
        mDefaultSP.edit().apply(func).apply()
    }

    fun remove(key: String) {
        edit { remove(key) }
    }

    /**********************************************************************************************/
    var spDomain: Int
        set(value) = edit { putInt(SP_DOMAIN, value) }
        get() = int(SP_DOMAIN, 0)

    var spSecurity: Boolean
        set(value) = edit { putBoolean(SP_SECURITY, value) }
        get() = boolean(SP_SECURITY)

    var spInitial: Boolean
        set(value) = edit { putBoolean(SP_INITIAL_INSTALLATION, value) }
        get() = boolean(SP_INITIAL_INSTALLATION, true)

    var spScreenOrientation: Int
        set(value) = edit { putInt(SP_SCREEN_ORIENTATION, value) }
        get() = int(SP_SCREEN_ORIENTATION)

    var spReadOrientation: Int
        set(value) = edit { putInt(SP_READING_DIRECTION, value) }
        get() = int(SP_READING_DIRECTION)

    var spImageZoom: Int
        set(value) = edit { putInt(SP_IMAGE_ZOOM, value) }
        get() = int(SP_IMAGE_ZOOM)

    var spKeepBright: Boolean
        set(value) = edit { putBoolean(SP_KEEP_BRIGHT, value) }
        get() = boolean(SP_KEEP_BRIGHT)

    var spShowTime: Boolean
        set(value) = edit { putBoolean(SP_SHOW_TIME, value) }
        get() = boolean(SP_SHOW_TIME)

    var spShowBattery: Boolean
        set(value) = edit { putBoolean(SP_SHOW_BATTERY, value) }
        get() = boolean(SP_SHOW_BATTERY)

    var spShowProgress: Boolean
        set(value) = edit { putBoolean(SP_SHOW_PROGRESS, value) }
        get() = boolean(SP_SHOW_PROGRESS, true)

    var spShowPagePadding: Boolean
        set(value) = edit { putBoolean(SP_SHOW_PAGE_PADDING, value) }
        get() = boolean(SP_SHOW_PAGE_PADDING)

    var spVolumeButtonTurnPages: Boolean
        set(value) = edit { putBoolean(SP_VOLUME_BUTTON_TURN_PAGES, value) }
        get() = boolean(SP_VOLUME_BUTTON_TURN_PAGES)

    var spFullScreen: Boolean
        set(value) = edit { putBoolean(SP_FULL_SCREEN, value) }
        get() = boolean(SP_FULL_SCREEN, true)

    var spGalleryTouchHotspotTips: Boolean
        set(value) = edit { putBoolean(SP_GALLERY_TOUCH_HOTSPOT_TIPS, value) }
        get() = boolean(SP_GALLERY_TOUCH_HOTSPOT_TIPS)

    var spDisableScreenshots: Boolean
        set(value) = edit { putBoolean(SP_DISABLE_SCREENSHOTS, value) }
        get() = boolean(SP_DISABLE_SCREENSHOTS)

    var spDownloadOriginal: Boolean
        set(value) = edit { putBoolean(SP_DOWNLOAD_ORIGINAL, value) }
        get() = boolean(SP_DOWNLOAD_ORIGINAL)

    var spDownloadThread: Int
        set(value) = edit { putInt(SP_DOWNLOAD_THREAD, value) }
        get() {
            return when (int(SP_DOWNLOAD_THREAD, 1)) {
                0 -> 1
                1 -> 3
                2 -> 5
                3 -> 7
                4 -> 11
                else -> throw IllegalArgumentException()
            }
        }

    var spPreloadImage: Int
        set(value) = edit { putInt(SP_PRELOAD_IMAGE, value) }
        get() {
            return when (int(SP_PRELOAD_IMAGE, 1)) {
                0 -> 3
                1 -> 5
                2 -> 7
                3 -> 9
                else -> throw IllegalArgumentException()
            }
        }

    var spProxyMode: Int
        set(value) = edit { putInt(SP_PROXY_MODE, value) }
        get() = int(SP_PROXY_MODE)

    var spProxyIp: String
        set(value) = edit { putString(SP_PROXY_IP, value) }
        get() = string(SP_PROXY_IP)

    var spProxyPort: Int
        set(value) = edit { putInt(SP_PROXY_PORT, value) }
        get() = int(SP_PROXY_PORT, -1)

    var spTheme: Int
        set(value) = edit { putInt(SP_THEME, value) }
        get() = int(SP_THEME, Setting.THEME_SYSTEM)

    var spCustomBrightness: Float
        set(value) = edit { putFloat(SP_CUSTOM_BRIGHTNESS, value) }
        get() = float(SP_CUSTOM_BRIGHTNESS, -1f)

    var spShowPage: Boolean
        set(value) = edit { putBoolean(SP_SHOW_PAGE, value) }
        get() = boolean(SP_SHOW_PAGE)

    var spShowJPTitle: Boolean
        set(value) = edit { putBoolean(SP_SHOW_JP_TITLE, value) }
        get() = boolean(SP_SHOW_JP_TITLE)

    /**********************************************************************************************/
    private var innerAppVersion: String
        set(value) = edit { putString(INNER_APP_VERSION, value) }
        get() = string(INNER_APP_VERSION)

    init {
        innerAppVersion = BuildConfig.VERSION_NAME
    }
}