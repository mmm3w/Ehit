package com.mitsuki.ehit.crutch

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.save.ShareData
import javax.inject.Inject

class OpenGate @Inject constructor(val shareData: ShareData) {

    val open: Boolean
        get() = !shareData.spWaringConfirm ||
                !shareData.spLoginShowed

    val nextNav: Int
        get() {
            if (!shareData.spLoginShowed)
                return R.id.action_global_login_fragment

            return -1
        }
}