package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import okhttp3.Response

abstract class BaseStringConvert<T> : Convert<T> {
    final override fun convertResponse(response: Response): T? {
        val webStr = response.body?.string()
        response.close()
        return convertResponse(webStr)
    }
    abstract fun convertResponse(data: String?): T?
}