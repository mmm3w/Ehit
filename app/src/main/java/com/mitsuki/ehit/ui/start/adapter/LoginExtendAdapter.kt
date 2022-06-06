package com.mitsuki.ehit.ui.start.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.save.MemoryData
import com.mitsuki.ehit.databinding.ItemLoginDomainBinding
import com.mitsuki.ehit.databinding.ItemLoginExtendBinding

class LoginExtend : SingleItemBindingAdapter<ItemLoginExtendBinding>(
    R.layout.item_login_extend,
    ItemLoginExtendBinding::bind
), EventEmitter {

    override val eventEmitter: Emitter = Emitter()
    private var mIsCookieLogin = false

    override val onViewHolderCreate: ViewHolder<ItemLoginExtendBinding>.() -> Unit = {
        binding.loginExtendSkip.setOnClickListener { post("skip", -1) }

        binding.loginExtendSwitch.apply {
            setOnClickListener {
                mIsCookieLogin = !mIsCookieLogin
                text =
                    if (mIsCookieLogin) text(R.string.text_login_by_account) else text(R.string.text_login_by_cookie)
                post("switch", mIsCookieLogin)
            }
        }
    }
}

class LoginDomain(val memoryData: MemoryData) : SingleItemBindingAdapter<ItemLoginDomainBinding>(
    R.layout.item_login_domain,
    ItemLoginDomainBinding::bind
) {
    override val onViewHolderCreate: ViewHolder<ItemLoginDomainBinding>.() -> Unit = {
        binding.domainCheckRadio.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.domain_check_e -> memoryData.domain = 0
                R.id.domain_check_ex -> memoryData.domain = 1
            }
        }
    }

    override val onViewHolderBind: ViewHolder<ItemLoginDomainBinding>.() -> Unit = {
        when (memoryData.domain) {
            0 -> binding.domainCheckRadio.check(R.id.domain_check_e)
            1 -> binding.domainCheckRadio.check(R.id.domain_check_ex)
        }
    }
}