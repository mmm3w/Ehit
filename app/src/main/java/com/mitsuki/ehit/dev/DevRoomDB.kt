package com.mitsuki.ehit.dev

import java.io.File

object DevRoomDB {
    fun checkRoomFile(
        source: String,
        target: String,
        db: String,
        shm: String,
        wal: String
    ){
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

        val targetDBFile = File(targetFolder, db)
        if (targetDBFile.exists() && targetDBFile.isFile) return





        val sourceFolder = File(source)
        if (!sourceFolder.exists() || sourceFolder.isFile) return


        //copy相关文件
        val srcDbFile = File(sourceFolder, db)
        if (srcDbFile.exists() && srcDbFile.isFile){

        }else{

        }


    }

    private fun copyFile(src: File, dst: File) {
        src.inputStream().use { input ->
            dst.outputStream().use { output -> input.copyTo(output) }
        }
    }
}