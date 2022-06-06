package com.mitsuki.ehit.ui.login.adapter

import android.view.View
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.BuildConfig
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemLoginCookieBinding

class LoginCookieAdapter : SingleItemBindingAdapter<ItemLoginCookieBinding>(
    R.layout.item_login_cookie,
    ItemLoginCookieBinding::bind,
    false
), EventEmitter {
    override val eventEmitter: Emitter = Emitter()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder<*>
        (holder.binding as? ItemLoginCookieBinding)?.apply {
            val member = loginIpbMemberId.text.toString()
            val pass = loginIpbPassHash.text.toString()
            val igneous = loginIgneous.text.toString()

            post("login", LoginCookie(member, pass, igneous))
        }
        Unit
    }

    override val onViewHolderCreate: ViewHolder<ItemLoginCookieBinding>.() -> Unit = {
        binding.loginIpbMemberId.setText(BuildConfig.ipb_member_id)
        binding.loginIpbPassHash.setText(BuildConfig.ipb_pass_hash)
        binding.loginIgneous.setText(BuildConfig.igneous)

        binding.loginLoginBtn.tag = this
        binding.loginLoginBtn.setOnClickListener(mItemClick)

    }

    data class LoginCookie(val memberId: String, val passHash: String, val igneous: String)
}