package com.mitsuki.ehit.ui.temp.adapter

import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R

class LoginAccountAdapter : SingleItemAdapter(true) {


    private val mLoginEvent: MutableLiveData<Account> = MutableLiveData()

    val onLogin: LiveData<Account> get() = mLoginEvent


    override val layoutRes: Int = R.layout.item_login_account

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        view<Button>(R.id.login_login_btn)?.setOnClickListener {
            val accountStr: String = view<EditText>(R.id.login_user_account)?.text.toString()
            val passwordStr: String = view<EditText>(R.id.login_user_password)?.text.toString()
            mLoginEvent.postValue(Account(accountStr, passwordStr))
        }
    }
    override val onViewHolderBind: ViewHolder.() -> Unit = {

    }


    data class Account(val account: String, val password: String)
}