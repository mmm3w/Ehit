package com.mitsuki.ehit.crutch.zip

import java.io.BufferedInputStream
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipPacker {
    /**
     * zip压缩
     * fileOutputStream 文件输出流
     * folder 需要被压缩的文件目录
     * file 需要被压缩的文件目录下的目标文件，null时全部压缩
     */
    /** 压缩 ***************************************************************************************/
//    private fun packWithBufferedStream(
//        outputStream: BufferedOutputStream,
//        folder: File,
//        file: Array<String>? = null
//    ) {
//        ZipOutputStream(outputStream).use { zipOutputStream ->
//            if (file != null) {
//                if (!folder.isDirectory) throw IllegalAccessException("illegal dir")
//                for (name in file) zip(File(folder, name), zipOutputStream)
//            } else {
//                zip(folder, zipOutputStream)
//            }
//        }
//    }
//
//    private fun zip(file: File, zipOutputStream: ZipOutputStream) {
//        if (file.exists()) {
//            if (file.isDirectory) {
//                file.list()?.apply {
//                    if (isEmpty()) {
//                        zipOutputStream.putNextEntry(ZipEntry(file.name + File.separator))
//                        zipOutputStream.closeEntry()
//                    } else {
//                        for (name in this) zip(File(file, name), zipOutputStream)
//                    }
//                }
//            } else {
//                zipFile(file, zipOutputStream)
//            }
//        }
//    }
//
//
//    fun pack(
//        outputStream: OutputStream,
//        folder: File,
//        file: Array<String>? = null
//    ) {
//        if (!folder.exists()) throw IllegalAccessException("illegal dir")
//        packWithBufferedStream(outputStream.buffered(), folder, file)
//    }


    //仅压缩文件
    fun packFiles(
        outputStream: OutputStream,
        files: Array<File>,
        progress: ((File, Float) -> Unit)? = null
    ) {
        outputStream.zipOutputStream().use { zipOutputStream ->
            for ((count, file) in files.withIndex()) {
                progress?.invoke(file, count.toFloat() / files.size)
                if (file.exists() && file.exists()) {
                    zipFile(file, zipOutputStream)
                }
            }
        }
    }


    /**********************************************************************************************/
    //将单个文件压入流
    private fun zipFile(file: File, zipOutputStream: ZipOutputStream) {
        zipOutputStream.putNextEntry(ZipEntry(file.name))
        file.inputStream().buffered().use { fileInputStream ->
            BufferedInputStream(fileInputStream).use { it.copyTo(zipOutputStream, 1024) }
        }
        zipOutputStream.closeEntry()
    }

    private fun OutputStream.zipOutputStream(): ZipOutputStream {
        if (this is ZipOutputStream) return this
        return ZipOutputStream(this)
    }
}