package com.mitsuki.ehit.core.ui.adapter

import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R


class LoginHeader : SingleItemAdapter(true) {
    override val layoutRes: Int = com.mitsuki.ehit.R.layout.item_login_header
    override val onViewHolderCreate: ViewHolder.() -> Unit = {}
    override val onViewHolderBind: ViewHolder.() -> Unit = {}

}

class LoginExtend : SingleItemAdapter(true) {

    private var mIsCookieLogin = false


    private val mSwitchEvent: MutableLiveData<Boolean> = MutableLiveData()
    private val mSkipEvent: MutableLiveData<Int> = MutableLiveData()

    val onSwitch: LiveData<Boolean> get() = mSwitchEvent
    val onSkip: LiveData<Int> get() = mSkipEvent

    override val layoutRes: Int = R.layout.item_login_extend
    override val onViewHolderCreate: ViewHolder.() -> Unit = {

        view<Button>(R.id.login_extend_skip)?.setOnClickListener { mSkipEvent.postValue(-1) }

        view<Button>(R.id.login_extend_switch)?.setOnClickListener {
            mIsCookieLogin = !mIsCookieLogin
            mSwitchEvent.postValue(mIsCookieLogin)
        }
    }
    override val onViewHolderBind: ViewHolder.() -> Unit = {}
}

class LoginDomain : SingleItemAdapter(true) {
    override val layoutRes: Int = R.layout.item_login_domain
    override val onViewHolderCreate: ViewHolder.() -> Unit = {

    }
    override val onViewHolderBind: ViewHolder.() -> Unit = {}
}