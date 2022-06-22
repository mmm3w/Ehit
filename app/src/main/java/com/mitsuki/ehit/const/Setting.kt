package com.mitsuki.ehit.const

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import java.lang.IllegalArgumentException

object Setting {
    const val THEME_SYSTEM = 0
    const val THEME_NORMAL = 1
    const val THEME_NIGHT = 2

    val themeText = arrayOf(
        string(R.string.text_theme_follow_system),
        string(R.string.text_theme_night_no),
        string(R.string.text_theme_night_yes)
    )

    const val PROXY_DIRECT = 0
    const val PROXY_SYSTEM = 1
    const val PROXY_HTTP= 2
    const val PROXY_SOCKS = 3

    fun proxySummary(index: Int): Int {
        return when (index) {
            PROXY_DIRECT -> R.string.text_proxy_direct
            PROXY_SYSTEM -> R.string.text_proxy_system
            PROXY_HTTP -> R.string.text_proxy_http
            PROXY_SOCKS -> R.string.text_proxy_socks
            else -> throw  IllegalArgumentException()
        }
    }

}