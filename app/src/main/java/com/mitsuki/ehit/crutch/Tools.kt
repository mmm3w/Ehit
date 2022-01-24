package com.mitsuki.ehit.crutch

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import java.text.SimpleDateFormat
import java.util.*

object Tools {
    private val commentTimeFormat by lazy {  SimpleDateFormat(string(R.string.comment_time_format_pattern), Locale.getDefault()) }

    fun commentTime(timestamp:Long):String{
        return  commentTimeFormat.format(timestamp)
    }

}