package com.mitsuki.ehit.model.convert

import com.mitsuki.armory.httprookie.convert.Convert
import com.mitsuki.ehit.crutch.fromJson
import com.mitsuki.ehit.model.entity.reponse.VoteBack
import okhttp3.Response
import java.lang.IllegalStateException

class VoteBackConvert : Convert<VoteBack> {
    override fun convertResponse(response: Response): VoteBack {
        val str = response.body?.string()
        response.close()
        return str?.fromJson() ?: throw  IllegalStateException()
    }
}