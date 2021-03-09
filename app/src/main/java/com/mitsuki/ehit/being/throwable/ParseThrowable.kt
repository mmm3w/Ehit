package com.mitsuki.ehit.being.throwable

import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.AppHolder

@Suppress("MemberVisibilityCanBePrivate")
class ParseThrowable(val debugMsg: String) :
    Throwable(AppHolder.string(R.string.error_parse_throwable_text), null) {
    override val message: String = AppHolder.string(R.string.error_parse_throwable_text)
        get() = if (BuildConfig.DEBUG) "$field\n$debugMsg" else field
}