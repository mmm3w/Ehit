package com.mitsuki.ehit.being

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.network.Url

@Suppress("MemberVisibilityCanBePrivate")
object ShareData {

    private lateinit var mDefaultSP: SharedPreferences

    fun init(context: Context) {
        mDefaultSP = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun string(key: String, default: String = ""): String =
        mDefaultSP.getString(key, default) ?: default

    fun boolean(key: String, default: Boolean = false): Boolean =
        mDefaultSP.getBoolean(key, default)

    fun edit(func: SharedPreferences.Editor.() -> Unit) {
        mDefaultSP.edit().apply(func).apply()
    }

    fun remove(key: String) {
        edit { remove(key) }
    }


    /** Tag ***************************************************************************************/
    const val SP_COOKIES = "SP_COOKIES"
    const val SP_SECURITY = "SP_SECURITY"
    const val SP_FIRST_OPEN = "SP_FIRST_OPEN"
    const val SP_DOMAIN = "SP_DOMAIN"
    const val SP_SHOW_JP_TITLE = "SP_SHOW_JP_TITLE"

    /**********************************************************************************************/

    var spCookies: String
        set(value) = edit { putString(SP_COOKIES, value) }
        get() = string(SP_COOKIES)

    var spSecurity: Boolean
        set(value) = edit { putBoolean(SP_SECURITY, value) }
        get() = boolean(SP_SECURITY)

    var spFirstOpen: Boolean
        set(value) = edit { putBoolean(SP_FIRST_OPEN, value) }
        get() = boolean(SP_FIRST_OPEN, true)

    var spDomain: String
        set(value) = edit { putString(SP_DOMAIN, value) }
        get() = string(SP_DOMAIN)

}