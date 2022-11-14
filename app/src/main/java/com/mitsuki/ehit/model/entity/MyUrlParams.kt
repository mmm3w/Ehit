package com.mitsuki.ehit.model.entity

import com.mitsuki.armory.httprookie.request.UrlParams

class MyUrlParams(action: (UrlParams.() -> Unit)? = null) : UrlParams {
    init {
        action?.invoke(this)
    }

    override val urlParams: LinkedHashMap<String, MutableList<String>> = linkedMapOf()
}