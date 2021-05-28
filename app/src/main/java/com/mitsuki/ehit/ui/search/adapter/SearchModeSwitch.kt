package com.mitsuki.ehit.ui.search.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.databinding.ItemSearchBinding

class SearchModeSwitch : SingleItemAdapter(true) {

    val switchEvent: MutableLiveData<Boolean> = MutableLiveData()

    var isAdvancedMode: Boolean = false
        private set(value) {
            if (value != field) {
                if (isEnable) notifyItemChanged(0)
                field = value
            }
        }

    private val mClickEvent = { _: View ->
        isAdvancedMode = !isAdvancedMode
        switchEvent.postValue(isAdvancedMode)
    }

    override val layoutRes: Int = R.layout.item_search

    private lateinit var binding: ItemSearchBinding

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        binding = ItemSearchBinding.bind(itemView)
        itemView.setOnClickListener(mClickEvent)
        binding.searchItemDelete.isVisible = false

    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        binding.searchItemIcon.setImageResource(if (isAdvancedMode) R.drawable.ic_round_keyboard_return_24 else R.drawable.ic_outline_change_circle_24)
        binding.searchItemText.text =
            if (isAdvancedMode) string(R.string.text_quick_search) else string(R.string.text_advanced_search)
    }

}