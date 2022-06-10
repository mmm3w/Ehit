package com.mitsuki.ehit.crutch.utils

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import java.text.SimpleDateFormat
import java.util.*

object TimeFormat {
    private val commentTimeFormat by lazy {
        SimpleDateFormat(
            string(R.string.comment_time_format_pattern),
            Locale.getDefault()
        )
    }

    fun commentTime(timestamp: Long): String {
        return commentTimeFormat.format(timestamp)
    }

    private val crashTimeFormat by lazy {
        SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
    }

    fun crashTime(timestamp: Long): String {
        return crashTimeFormat.format(timestamp)
    }

    private val fileNameTimeFormat by lazy {
        SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss",
            Locale.getDefault()
        )
    }

    fun fileNameTime(timestamp: Long): String {
        return fileNameTimeFormat.format(timestamp)
    }

}

fun Long.commentTime(): String {
    return TimeFormat.commentTime(this)
}

fun Long.crashTime(): String {
    return TimeFormat.crashTime(this)
}

fun Long.fileNameTime(): String {
    return TimeFormat.fileNameTime(this)
}
