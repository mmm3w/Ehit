package com.mitsuki.ehit.const

import com.mitsuki.ehit.crutch.AppHolder
import java.io.File

object DirManager {

    fun downloadCache(gid: Long, token: String): File {
        return AppHolder.cacheDir("download/$gid-$token")
    }


}