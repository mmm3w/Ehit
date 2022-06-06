package com.mitsuki.ehit.ui.start.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemLoginAccountBinding

class LoginAccountAdapter : SingleItemBindingAdapter<ItemLoginAccountBinding>(
    R.layout.item_login_account,
    ItemLoginAccountBinding::bind
), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    override val onViewHolderCreate: ViewHolder<ItemLoginAccountBinding>.() -> Unit = {
        binding.loginLoginBtn.setOnClickListener {
            val accountStr: String = binding.loginUserAccount.text.toString()
            val passwordStr: String = binding.loginUserPassword.text.toString()
            post("login", Account(accountStr, passwordStr))
        }
    }

    data class Account(val account: String, val password: String)
}