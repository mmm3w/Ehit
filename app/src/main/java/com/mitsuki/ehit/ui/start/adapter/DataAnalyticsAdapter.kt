package com.mitsuki.ehit.ui.start.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemDataAnalyticsBinding

class DataAnalyticsAdapter : SingleItemBindingAdapter<ItemDataAnalyticsBinding>(
    R.layout.item_data_analytics,
    ItemDataAnalyticsBinding::bind
), EventEmitter {
    override val eventEmitter: Emitter = Emitter()
    override val onViewHolderCreate: ViewHolder<ItemDataAnalyticsBinding>.() -> Unit = {
        binding.dataAnalyticsConfirm.setOnClickListener { post("event", true) }
        binding.dataAnalyticsRefuse.setOnClickListener { post("event", false) }
    }
}