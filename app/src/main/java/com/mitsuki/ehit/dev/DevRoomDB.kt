package com.mitsuki.ehit.dev

import java.io.File

object DevRoomDB {
    fun checkRoomFile(
        source: String,
        target: String,
        db: String,
        shm: String,
        wal: String
    ) {
        val targetFolder = File(target)
        //检查目标目录
        if (targetFolder.exists()) {
            if (targetFolder.isFile) {
                targetFolder.delete()
                targetFolder.mkdirs()
            }
        } else {
            targetFolder.mkdirs()
        }

        dbFileCheck(File(source, db), File(target, db))
        dbFileCheck(File(source, shm), File(target, shm))
        dbFileCheck(File(source, wal), File(target, wal))

    }

    private fun dbFileCheck(src: File, dst: File) {
        if (dst.exists()) {
            if (dst.isFile) return
        }
        if (!src.exists()) return
        copyFile(src, dst)
    }

    private fun copyFile(src: File, dst: File) {
        src.inputStream().use { input ->
            dst.outputStream().use { output -> input.copyTo(output) }
        }
    }
}