package com.mitsuki.ehit.crutch.network.ehcore

import com.mitsuki.ehit.crutch.network.ehcore.params.EhImplParams
import com.mitsuki.ehit.crutch.network.ehcore.params.EhParams
import com.mitsuki.ehit.crutch.network.ehcore.params.ExImplParams
import com.mitsuki.ehit.crutch.network.ehcore.site.EhSite
import com.mitsuki.ehit.crutch.network.ehcore.site.ExSite
import com.mitsuki.ehit.crutch.network.ehcore.site.Site

object ApiContainer : Site {

    var site: Site = EhSite()

    val isEx: Boolean get() = site !is EhSite

    var ehParams: EhParams = EhImplParams()

    override val domain: String
        get() = site.domain

    fun refreshDomain(id: Int) {
        if (id == 1) {
            if (site !is ExSite) {
                site = ExSite()
            }
            if (ehParams !is ExImplParams) {
                ehParams = ExImplParams()
            }
        } else {
            if (site !is EhSite) {
                site = EhSite()
            }
            if (ehParams !is EhImplParams) {
                ehParams = EhImplParams()
            }
        }
    }
}