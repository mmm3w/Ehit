package com.mitsuki.ehit.crutch.zip

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Zip {
    /**
     * zip压缩
     * fileOutputStream 文件输出流
     * folder 需要被压缩的文件目录
     * file 需要被压缩的文件目录下的目标文件，null时全部压缩
     */
    fun packFile(
        outputStream: OutputStream,
        folder: File,
        file: Array<String>? = null
    ) {
        if (!folder.exists()) throw IllegalAccessException("源目录不存在")
        ZipOutputStream(outputStream).use { zipOutputStream ->
            if (file != null) {
                if (!folder.isDirectory) throw IllegalAccessException("源目录非文件夹目录")
                for (name in file) zip(File(folder, name), zipOutputStream)
            } else {
                zip(folder, zipOutputStream)
            }
        }
    }

    private fun zip(file: File, zipOutputStream: ZipOutputStream) {
        if (file.exists()) {
            if (file.isDirectory) {
                file.list()?.apply {
                    if (isEmpty()) {
                        zipOutputStream.putNextEntry(ZipEntry(file.name + File.separator))
                        zipOutputStream.closeEntry()
                    } else {
                        for (name in this) zip(File(file, name), zipOutputStream)
                    }
                }
            } else {
                zipFile(file, zipOutputStream)
            }
        }
    }

    //文件压缩写入
    private fun zipFile(file: File, zipOutputStream: ZipOutputStream) {
        zipOutputStream.putNextEntry(ZipEntry(file.name))
        file.inputStream().use { fileInputStream ->
            zipOutputStream.write(fileInputStream, 1024)
        }
        zipOutputStream.closeEntry()
    }

    private fun OutputStream.write(inputStream: InputStream, bufferSize: Int) {
        var len: Int
        val buffer = ByteArray(bufferSize)
        while (inputStream.read(buffer).also { len = it } != -1) {
            write(buffer, 0, len)
        }
    }
}