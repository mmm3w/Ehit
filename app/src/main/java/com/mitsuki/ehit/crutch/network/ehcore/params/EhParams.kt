package com.mitsuki.ehit.crutch.network.ehcore.params

import com.mitsuki.armory.httprookie.request.HasBody
import com.mitsuki.armory.httprookie.request.UrlParams

interface EhParams {
    companion object {
        val NONE by lazy { { _: UrlParams, _: HasBody? -> } }
    }

    fun attach(name: String, params: Array<String?>): (UrlParams, HasBody?) -> Unit

    fun Array<String?>.opt(index: Int): String? {
        return if (this.size > index) this[index] else null
    }
}