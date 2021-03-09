package com.mitsuki.ehit.being.network

import android.content.Context
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.ShareData

object Url {
    lateinit var domain: Array<Pair<String, String>>

    var currentDomain: String
        get() = ShareData.spDomain
        set(value) {
            ShareData.spDomain = value
        }

    fun initDomain(context: Context) {
        with(context) {
            domain = arrayOf(
                getString(R.string.text_e_ht) to getString(R.string.domain_e_ht),
                getString(R.string.text_ex_ht) to getString(R.string.domain_ex_ht)
            )
            if (currentDomain.isEmpty()) currentDomain = domain[0].second
        }
    }

    fun login(): String = "https://forums.e-hentai.org/index.php?act=Login&CODE=01"

    fun galleryList(): String = "$currentDomain/"

    fun galleryListBySubscription(): String = "$currentDomain/watched"

    fun galleryListByPopular(): String = "$currentDomain/popular"

    fun galleryDetail(gid: Long, token: String): String = "$currentDomain/g/$gid/$token"

    fun galleryPreviewDetail(gid: Long, token: String, index: Int): String =
        "$currentDomain/s/$token/$gid-${index + 1}"

    fun ehSetting(): String = "$currentDomain/uconfig.php"
}