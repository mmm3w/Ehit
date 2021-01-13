package com.mitsuki.ehit.being

import android.content.Context
import android.content.SharedPreferences
import com.mitsuki.ehit.R

@Suppress("MemberVisibilityCanBePrivate")
object ShareData {

    private lateinit var mSharedPreferences: SharedPreferences

    fun init(context: Context) {
        mSharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    fun string(key: String, default: String = ""): String =
        mSharedPreferences.getString(key, default) ?: default

    fun boolean(key: String, default: Boolean = false): Boolean =
        mSharedPreferences.getBoolean(key, default)

    fun edit(func: SharedPreferences.Editor.() -> Unit) {
        mSharedPreferences.edit().apply(func).apply()
    }

    fun remove(key: String) {
        edit { remove(key) }
    }


    /** Tag ***************************************************************************************/
    const val SP_COOKIES = "SP_COOKIES"
    const val SP_SECURITY = "SP_SECURITY"
    const val SP_FIRST_OPEN = "SP_FIRST_OPEN"

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

}