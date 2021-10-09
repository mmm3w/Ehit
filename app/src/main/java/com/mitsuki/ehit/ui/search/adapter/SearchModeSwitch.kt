package com.mitsuki.ehit.ui.search.adapter

import android.view.View
import androidx.core.view.isVisible
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.databinding.ItemSearchBinding

class SearchModeSwitch :
    SingleItemBindingAdapter<ItemSearchBinding>(R.layout.item_search, ItemSearchBinding::bind),
    EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private var isAdvancedMode: Boolean = false
        private set(value) {
            if (value != field) {
                if (isEnable) notifyItemChanged(0)
                field = value
            }
        }

    private val mClickEvent = { _: View ->
        isAdvancedMode = !isAdvancedMode
        post("switch", isAdvancedMode)
    }

    override val onViewHolderCreate: ViewHolder<ItemSearchBinding>.() -> Unit = {
        itemView.setOnClickListener(mClickEvent)
        binding.searchItemDelete.isVisible = false
    }

    override val onViewHolderBind: ViewHolder<ItemSearchBinding>.() -> Unit = {
        binding.searchItemIcon.setImageResource(if (isAdvancedMode) R.drawable.ic_round_keyboard_return_24 else R.drawable.ic_outline_change_circle_24)
        binding.searchItemText.text =
            if (isAdvancedMode) string(R.string.text_quick_search) else string(R.string.text_advanced_search)
    }

}