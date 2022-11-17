package com.mitsuki.ehit.crutch.network.ehcore.params

import com.mitsuki.armory.httprookie.request.HasBody
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.RequestKey

class ExImplParams : EhImplParams() {
    override fun attach(name: String, params: Array<String?>): (UrlParams, HasBody?) -> Unit {
        return when (name) {
            "galleryList" -> galleryList(params.opt(0), params.opt(1), params.opt(2), params.opt(3))
            else -> super.attach(name, params)
        }
    }

    private fun galleryList(
        next: String?,
        prev: String?,
        jump: String?,
        seek: String?
    ): (UrlParams, HasBody?) -> Unit {
        return { url, _ ->
            next?.takeIf { it.isNotEmpty() }?.also {
                url.urlParams(RequestKey.NEXT, it)
            } ?: prev?.takeIf { it.isNotEmpty() }?.also {
                url.urlParams(RequestKey.PREV, it)
            }

            jump?.takeIf { it.isNotEmpty() }?.also {
                url.urlParams(RequestKey.JUMP, it)
            } ?: seek?.takeIf { it.isNotEmpty() }?.also {
                url.urlParams(RequestKey.SEEK, it)
            }
        }
    }


}