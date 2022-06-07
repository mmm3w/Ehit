package com.mitsuki.ehit.crutch.zip

import com.mitsuki.ehit.crutch.extensions.ensureDir
import com.mitsuki.ehit.crutch.extensions.ignoreSuffixName
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipUnpack {

    fun streamUnpack(inputStream: BufferedInputStream, target: File, deep: Boolean = false) {
        if (target.exists()) {
            if (target.isFile) throw IllegalAccessException("目标目录已被占用")
        } else {
            target.mkdirs()
        }

        ZipInputStream(inputStream).use { zipInputStream ->
            var entry: ZipEntry?
            while (zipInputStream.nextEntry.also { entry = it } != null) {
                entry?.let { zipEntry ->
                    if (zipEntry.isDirectory) {
                        File(target, zipEntry.name).ensureDir()
                    } else {
                        File(target, zipEntry.name).apply {
                            parentFile?.ensureDir() ?: throw IllegalAccessException()
                            outputStream().buffered(1024).use { fileOutputStream ->
                                zipInputStream.copyTo(fileOutputStream, 1024)
                            }
                            if (absolutePath.endsWith("zip", true) && deep) {
                                streamUnpack(
                                    inputStream().buffered(),
                                    File(parentFile, ignoreSuffixName()),
                                    deep
                                )
                                delete()
                            }
                        }
                    }
                }
            }
        }
    }

}