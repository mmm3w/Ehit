package com.mitsuki.ehit.crutch

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mitsuki.ehit.crutch.network.Site

class ShareData(context: Context) {
    private val mDefaultSP: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val SP_SECURITY = "SP_SECURITY"

        const val SP_GALLERY_PAGE_SIZE = "SP_GALLERY_PAGE_SIZE"

        const val SP_DOMAIN = "SP_DOMAIN"
        const val SP_SHOW_JP_TITLE = "SP_SHOW_JP_TITLE"
        const val SAVE_SOME_DATA_IN_PUBLIC_STORAGE = "SAVE_SOME_DATA_IN_PUBLIC_STORAGE"

        //read setting
        const val SP_SCREEN_ORIENTATION = "SP_SCREEN_ORIENTATION"
        const val SP_READ_ORIENTATION = "SP_READ_ORIENTATION"
        const val SP_IMAGE_ZOOM = "SP_IMAGE_ZOOM"
        const val SP_KEEP_BRIGHT = "SP_KEEP_BRIGHT"
        const val SP_SHOW_TIME = "SP_SHOW_TIME"
        const val SP_SHOW_BATTERY = "SP_SHOW_BATTERY"
        const val SP_SHOW_PROGRESS = "SP_SHOW_PROGRESS"
        const val SP_SHOW_PAGE_PADDING = "SP_SHOW_PAGE_PADDING"
        const val SP_VOLUME_BUTTON_TURN_PAGES = "SP_VOLUME_BUTTON_TURN_PAGES"
        const val SP_FULL_SCREEN = "SP_FULL_SCREEN"
        const val SP_CUSTOM_BRIGHTNESS = "SP_CUSTOM_BRIGHTNESS"


        const val SP_OPEN_APP_WARING_CONFIRM = "SP_OPEN_APP_WARING_CONFIRM"
        const val SP_OPEN_LOGIN_SHOWED = "SP_OPEN_LOGIN_SHOWED"
    }

    fun string(key: String, default: String = ""): String =
        mDefaultSP.getString(key, default) ?: default

    fun boolean(key: String, default: Boolean = false): Boolean =
        mDefaultSP.getBoolean(key, default)

    fun int(key: String, default: Int = 0): Int =
        mDefaultSP.getInt(key, default)


    fun edit(func: SharedPreferences.Editor.() -> Unit) {
        mDefaultSP.edit().apply(func).apply()
    }

    fun remove(key: String) {
        edit { remove(key) }
    }

    /**********************************************************************************************/
    private var spDomain: Int
        set(value) = edit { putInt(SP_DOMAIN, value) }
        get() = int(SP_DOMAIN, 0)

    var spSecurity: Boolean
        set(value) = edit { putBoolean(SP_SECURITY, value) }
        get() = boolean(SP_SECURITY)

    var spWaringConfirm: Boolean
        set(value) = edit { putBoolean(SP_OPEN_APP_WARING_CONFIRM, value) }
        get() = boolean(SP_OPEN_APP_WARING_CONFIRM)

    var spLoginShowed: Boolean
        set(value) = edit { putBoolean(SP_OPEN_LOGIN_SHOWED, value) }
        get() = boolean(SP_OPEN_LOGIN_SHOWED)

    var spSaveSomeDataInPublicStorage: Boolean
        set(value) = edit { putBoolean(SAVE_SOME_DATA_IN_PUBLIC_STORAGE, value) }
        get() = boolean(SAVE_SOME_DATA_IN_PUBLIC_STORAGE)

    var spGalleryPageSize: Int
        set(value) = edit { putInt(SP_GALLERY_PAGE_SIZE, value) }
        get() = int(SP_GALLERY_PAGE_SIZE)

    var spScreenOrientation: Int
        set(value) = edit { putInt(SP_SCREEN_ORIENTATION, value) }
        get() = int(SP_SCREEN_ORIENTATION)

    var spReadOrientation: Int
        set(value) = edit { putInt(SP_READ_ORIENTATION, value) }
        get() = int(SP_READ_ORIENTATION)

    var spImageZoom: Int
        set(value) = edit { putInt(SP_IMAGE_ZOOM, value) }
        get() = int(SP_IMAGE_ZOOM)


    /**********************************************************************************************/
    var domain: Int = spDomain
        set(value) {
            if (value != field) {
                field = value
                spDomain = value
                Site.refreshDomain(field)
            }
        }

    var screenOrientation: Int = spScreenOrientation
        set(value) {
            if (value != field) {
                field = value
                spScreenOrientation = value
            }
        }

    var readOrientation: Int = spReadOrientation
        set(value) {
            if (value != field) {
                field = value
                spReadOrientation = value
            }
        }

    var imageZoom: Int = spImageZoom
        set(value) {
            if (value != field) {
                field = value
                spImageZoom = value
            }
        }


    init {
        Site.refreshDomain(domain)
    }
}