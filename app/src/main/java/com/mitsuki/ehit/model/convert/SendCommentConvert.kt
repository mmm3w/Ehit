package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import okhttp3.Response
import org.jsoup.Jsoup

class SendCommentConvert:Convert<Int> {
    override fun convertResponse(response: Response): Int {
        val webStr = response.body?.string()
        response.close()
        val document = Jsoup.parse(webStr)
        val elements = document.select("#chd + p")
        if (elements.size > 0) throw IllegalStateException(elements[0].text())
        return 0
    }
}