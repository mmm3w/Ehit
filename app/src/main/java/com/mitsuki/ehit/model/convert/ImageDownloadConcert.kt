package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import okhttp3.Response
import okhttp3.internal.headersContentLength
import java.io.File

class ImageDownloadConcert(saveFile: File) : Convert<File> {
    override fun convertResponse(response: Response): File? {
        response.headersContentLength()



    }
}