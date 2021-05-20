package com.mitsuki.ehit.crutch.throwable

import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder

@Suppress("MemberVisibilityCanBePrivate")
class ParseThrowable(val debugMsg: String) :
    Throwable(AppHolder.string(R.string.error_parse_throwable_text), null) {
    override val message: String = AppHolder.string(R.string.error_parse_throwable_text)
        get() = if (BuildConfig.DEBUG) "$field\n$debugMsg" else field
}