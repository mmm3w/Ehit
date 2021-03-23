package com.mitsuki.ehit.being

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.network.CookieJarImpl
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

    const val SP_COOKIE_IPB_MEMBER_ID = "ipb_member_id"
    const val SP_COOKIE_IPB_PASS_HASH = "ipb_pass_hash"
    const val SP_COOKIE_IGNEOUS = "igneous"

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
//        get() = true

    var spDomain: String
        set(value) = edit { putString(SP_DOMAIN, value) }
        get() = string(SP_DOMAIN)


    fun saveCookie(id: String, hash: String, igneous: String) {
        edit {
            putString(
                SP_COOKIE_IPB_MEMBER_ID,
                CookieJarImpl.newBase64Cookie(SP_COOKIE_IPB_MEMBER_ID, id)
            )
            putString(
                SP_COOKIE_IPB_PASS_HASH,
                CookieJarImpl.newBase64Cookie(SP_COOKIE_IPB_PASS_HASH, hash)
            )
            putString(
                SP_COOKIE_IGNEOUS,
                CookieJarImpl.newBase64Cookie(SP_COOKIE_IGNEOUS, igneous)
            )
            putString(
                SP_COOKIES,
                "${SP_COOKIE_IPB_MEMBER_ID},${SP_COOKIE_IPB_PASS_HASH},${SP_COOKIE_IGNEOUS}"
            )
        }
    }

    fun clearCookie() {
        edit {
            remove(SP_COOKIE_IPB_MEMBER_ID)
            remove(SP_COOKIE_IPB_PASS_HASH)
            remove(SP_COOKIE_IGNEOUS)
            remove(SP_COOKIES)
        }
    }
}