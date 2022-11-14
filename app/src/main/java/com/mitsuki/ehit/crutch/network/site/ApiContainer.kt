package com.mitsuki.ehit.crutch.network.site

object ApiContainer : Site {

    var site: Site = EhSite()

    override val domain: String
        get() = site.domain

    fun refreshDomain(id: Int) {
        if (id == 1) {
            if (site !is ExSite) {
                site = ExSite()
            }
        } else {
            if (site !is EhSite) {
                site = EhSite()
            }
        }
    }
}