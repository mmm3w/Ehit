package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import okhttp3.Response

/**
 * 各类异常处理装饰
 */

//302无登录状态重定向
class Response302Decoration<T>(private val convert: Convert<T>) : Convert<T> {
    override fun convertResponse(response: Response): T? {
        if (response.code == 302) {
            if (response.header("Location")?.contains("forums") == true) {
                throw IllegalStateException(string(R.string.error_not_login))
            } else {
                throw IllegalStateException(string(R.string.error_redirect))
            }
        }
        return convert.convertResponse(response)
    }
}

//IP封禁待解除
class IPBannedDecoration<T>(private val convert: BaseStringConvert<T>) : BaseStringConvert<T>() {
    override fun convertResponse(data: String?): T? {
        if (data?.contains("Your IP address has been temporarily banned") == true) {
            throw IllegalStateException(data)
        }
        return convert.convertResponse(data)
    }
}

fun <T> Convert<T>.deco302(): Convert<T> {
    return if (this is Response302Decoration) this else Response302Decoration(this)
}

fun <T> Convert<T>.decoIPBanned(): Convert<T> {
    return if (this is IPBannedDecoration) this else {
        if (this is BaseStringConvert) IPBannedDecoration(this) else throw IllegalArgumentException()
    }
}