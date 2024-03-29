package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.crutch.moshi.fromJson
import com.mitsuki.ehit.model.entity.reponse.RateBack
import okhttp3.Response

class RateBackConvert : Convert<RateBack> {
    override fun convertResponse(response: Response): RateBack {
        val str = response.body?.string()
        response.close()
        return str?.fromJson() ?: throw IllegalStateException()
    }
}