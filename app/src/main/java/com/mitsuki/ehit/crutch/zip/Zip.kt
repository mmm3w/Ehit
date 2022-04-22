package com.mitsuki.ehit.crutch.zip

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


object Zip {
    /**
     * zip压缩
     * fileOutputStream 文件输出流
     * folder 需要被压缩的文件目录
     * file 需要被压缩的文件目录下的目标文件，null时全部压缩
     */


    /** 压缩 ***************************************************************************************/
    private fun packWithBufferedStream(
        outputStream: BufferedOutputStream,
        folder: File,
        file: Array<String>? = null
    ) {
        ZipOutputStream(outputStream).use { zipOutputStream ->
            if (file != null) {
                if (!folder.isDirectory) throw IllegalAccessException("illegal dir")
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

    private fun zipFile(file: File, zipOutputStream: ZipOutputStream) {
        zipOutputStream.putNextEntry(ZipEntry(file.name))
        file.inputStream().buffered().use { fileInputStream ->
            BufferedInputStream(fileInputStream).use { zipOutputStream.write(it, 1024) }
        }
        zipOutputStream.closeEntry()
    }

    fun pack(
        outputStream: OutputStream,
        folder: File,
        file: Array<String>? = null
    ) {
        if (!folder.exists()) throw IllegalAccessException("illegal dir")
        packWithBufferedStream(outputStream.buffered(), folder, file)
    }

    /** 解压 ***************************************************************************************/
    fun unpack(
        inputStream: InputStream,
        target: File
    ) {
        if (target.exists()) {
            if (target.isFile) throw IllegalAccessException("目标目录已被占用")
        } else {
            target.mkdirs()
        }
        if (inputStream is BufferedInputStream) {
            zipStreamUnpack(inputStream, target)
        } else {
            BufferedInputStream(inputStream).use { zipStreamUnpack(it, target) }
        }
    }


    fun unpack(zip: File, target: File) {

    }

    private fun zipStreamUnpack(inputStream: BufferedInputStream, target: File) {
        ZipInputStream(inputStream).use { zipInputStream ->
            var entry: ZipEntry
            while (zipInputStream.nextEntry.also { entry = it } != null) {
                if (entry.isDirectory) {
                    File(target, entry.name).apply {
                        if (!exists()) mkdirs()
                    }
                } else {
                    File(target, entry.name).apply {
                        parentFile?.apply { if (!exists()) mkdirs() }
                            ?: throw IllegalAccessException()
                        outputStream().use { fileOutputStream ->
                            BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                                bufferedOutputStream.write(zipInputStream, 1024)
                            }
                        }
                    }
                }
            }
        }
    }


    /**********************************************************************************************/

    private fun OutputStream.write(inputStream: InputStream, bufferSize: Int) {
        var len: Int
        val buffer = ByteArray(bufferSize)
        while (inputStream.read(buffer).also { len = it } != -1) {
            write(buffer, 0, len)
        }
    }
}