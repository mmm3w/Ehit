package com.mitsuki.ehit.ui.start.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemDisclaimerBinding

class DisclaimerAdapter : SingleItemBindingAdapter<ItemDisclaimerBinding>(
    R.layout.item_disclaimer,
    ItemDisclaimerBinding::bind
), EventEmitter {
    override val eventEmitter: Emitter = Emitter()
    override val onViewHolderCreate: ViewHolder<ItemDisclaimerBinding>.() -> Unit = {
        binding.disclaimerYesBtn.setOnClickListener { post("disclaimer", true) }
        binding.disclaimerNoBtn.setOnClickListener { post("disclaimer", false) }
    }
}