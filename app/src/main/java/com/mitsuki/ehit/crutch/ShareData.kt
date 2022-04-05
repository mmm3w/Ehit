package com.mitsuki.ehit.crutch

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mitsuki.ehit.crutch.network.CookieJarImpl
import com.mitsuki.ehit.crutch.network.Url
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ShareData(context: Context) {
    private val mDefaultSP: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    init {
        Url.domainCache = spDomain
    }

    companion object {
        const val SP_SECURITY = "SP_SECURITY"

        const val SP_GALLERY_PAGE_SIZE = "SP_GALLERY_PAGE_SIZE"

        const val SP_DOMAIN = "SP_DOMAIN"
        const val SP_SHOW_JP_TITLE = "SP_SHOW_JP_TITLE"
        const val SAVE_SOME_DATA_IN_PUBLIC_STORAGE = "SAVE_SOME_DATA_IN_PUBLIC_STORAGE"


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
    var spDomain: String
        set(value) = edit { putString(SP_DOMAIN, value) }
        get() = string(SP_DOMAIN, Url.EH)

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

}