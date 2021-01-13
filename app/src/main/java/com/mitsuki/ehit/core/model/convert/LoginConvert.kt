package com.mitsuki.ehit.core.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.core.model.ehparser.Matcher
import okhttp3.Response
import java.lang.Exception

class LoginConvert : Convert<String> {
    override fun convertResponse(response: Response): String {
        val str = response.body?.string() ?: ""
        response.close()
        val m = Matcher.LOGIN_INFO.matcher(str)
        if (m.find()) {
            return m.group(1) ?: ""
        } else {
            throw Exception("登录失败")
        }
    }
}