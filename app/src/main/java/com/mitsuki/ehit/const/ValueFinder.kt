package com.mitsuki.ehit.const

import com.mitsuki.ehit.R
import java.lang.IllegalArgumentException

object ValueFinder {

    const val THEME_SYSTEM = 0
    const val THEME_NORMAL = 1
    const val THEME_NIGHT = 2





    fun proxySummary(index: Int): Int {
        return when (index) {
            0 -> R.string.text_proxy_direct
            1 -> R.string.text_proxy_system
            2 -> R.string.text_proxy_http
            3 -> R.string.text_proxy_socks
            else -> throw  IllegalArgumentException()
        }
    }




}