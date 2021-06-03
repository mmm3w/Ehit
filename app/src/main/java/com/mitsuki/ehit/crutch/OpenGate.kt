package com.mitsuki.ehit.crutch

import com.mitsuki.ehit.R

object OpenGate {

    val open: Boolean
        get() = !ShareData.spWaringConfirm ||
                !ShareData.spLoginShowed

    val nextNav: Int
        get() {
            if (!ShareData.spLoginShowed)
                return R.id.action_global_login_fragment

            return -1
        }
}