package com.mitsuki.ehit.crutch.throwable

import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extend.string

@Suppress("MemberVisibilityCanBePrivate")
class ParseThrowable(val debugMsg: String) :
    Throwable(string(R.string.error_parse_throwable_text), null) {
    override val message: String = string(R.string.error_parse_throwable_text)
        get() = if (BuildConfig.DEBUG) "$field\n$debugMsg" else field
}