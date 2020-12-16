package com.mitsuki.ehit.core.ui.adapter

import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R

class LoginCookieAdapter : SingleItemAdapter(false) {

    private val mLoginEvent: MutableLiveData<LoginCookie> = MutableLiveData()

    val onLogin: LiveData<LoginCookie> get() = mLoginEvent

    override val layoutRes: Int = com.mitsuki.ehit.R.layout.item_login_cookie

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        view<Button>(R.id.login_login_btn)?.setOnClickListener {
            val member = view<EditText>(R.id.login_ipb_member_id)?.text.toString()
            val pass = view<EditText>(R.id.login_ipb_pass_hash)?.text.toString()
            val igneous = view<EditText>(R.id.login_igneous)?.text.toString()
            mLoginEvent.postValue(LoginCookie(member, pass, igneous))
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {

    }

    data class LoginCookie(val memberId: String, val passHash: String, val igneous: String)
}