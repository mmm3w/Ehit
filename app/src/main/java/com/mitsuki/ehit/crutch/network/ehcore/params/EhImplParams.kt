package com.mitsuki.ehit.crutch.network.ehcore.params

import com.mitsuki.armory.httprookie.request.HasBody
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.ehit.const.ParamsValue
import com.mitsuki.ehit.const.RequestKey

/**
 * 这里需要参考Atlas做一下改造，现在先手动写一下
 */
open class EhImplParams : EhParams {
    override fun attach(name: String, params: Array<String?>): (UrlParams, HasBody?) -> Unit {
        return when (name) {
            "login" -> login(params[0]!!, params[1]!!)
            "galleryList" -> galleryList(params[0]!!)
            else -> EhParams.NONE
        }
    }

    private fun login(account: String, password: String): (UrlParams, HasBody?) -> Unit {
        return { _, body ->
            body?.params(RequestKey.REFERER, ParamsValue.LOGIN_REFERER)
            body?.params(RequestKey.B, "")
            body?.params(RequestKey.BT, "")

            body?.params(RequestKey.USER_NAME, account)
            body?.params(RequestKey.PASS_WORD, password)
            body?.params(RequestKey.COOKIE_DATE, "1")
            //body?.params(RequestKey.PRIVACY to "1")
        }
    }

    private fun galleryList(page: String): (UrlParams, HasBody?) -> Unit {
        return { url, _ ->
            if (page != "0") {
                url.urlParams(RequestKey.PAGE, page)
            }
        }
    }


}