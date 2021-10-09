package com.mitsuki.ehit.ui.search.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemSearchExpandBinding

class SearchAdvancedOptionsSwitch : SingleItemBindingAdapter<ItemSearchExpandBinding>(
    R.layout.item_search_expand, ItemSearchExpandBinding::bind, false
), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    var isChecked: Boolean = false
        set(value) {
            if (value != field) {
                if (isEnable) notifyItemChanged(0)
                field = value
            }
        }

    override val onViewHolderCreate: ViewHolder<ItemSearchExpandBinding>.() -> Unit = {
        binding.searchSwitch.setOnCheckedChangeListener { _, state ->
            isChecked = state
            post("switch", isChecked)
        }
    }

    override val onViewHolderBind: ViewHolder<ItemSearchExpandBinding>.() -> Unit = {
        binding.searchSwitch.isChecked = isChecked
    }
}