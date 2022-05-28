package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.crutch.extensions.clearDir
import com.mitsuki.ehit.crutch.extensions.ensureDir
import okhttp3.Response
import okhttp3.internal.headersContentLength
import java.io.File

class ImageDownloadConcert(private val saveFile: File) : Convert<File> {
    override fun convertResponse(response: Response): File {
        val contentLength = response.headersContentLength()
        if (contentLength <= 0) throw IllegalStateException()

        saveFile.parentFile?.ensureDir()
        saveFile.clearDir()

        var readLenght: Long
        (response.body?.byteStream() ?: throw IllegalStateException()).use { inputStream ->
            saveFile.outputStream().use { outputStream ->
                readLenght = inputStream.copyTo(outputStream, 1024)
            }
        }

        if (readLenght != contentLength) {
            saveFile.clearDir()
            throw IllegalStateException()
        }

        return saveFile
    }
}