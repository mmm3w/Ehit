package com.mitsuki.ehit.crutch.db

import com.mitsuki.ehit.crutch.AppHolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RoomData {
    const val CACHE_DB_NAME = "ehit-cache"
    const val STORE_DB_NAME = "ehit"

    val storeFileArray
        get() = arrayOf(
            STORE_DB_NAME,
            "${STORE_DB_NAME}-shm",
            "${STORE_DB_NAME}-wal"
        )

    val dbFolder get() = File(AppHolder.cacheDir.parent, "databases")

    fun storeSaveFileName(): String {
        return "ehit-${
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
        }.zip"
    }
}
