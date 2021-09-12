package com.mitsuki.ehit.ui.search.adapter

import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R

class SearchAdvancedOptionsSwitch : SingleItemAdapter(false) {

    val switchEvent: MutableLiveData<Boolean> = MutableLiveData()

    var isChecked: Boolean = false
        set(value) {
            if (value != field) {
                if (isEnable) notifyItemChanged(0)
                field = value
            }
        }


    override val layoutRes: Int = R.layout.item_search_expand

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        view<SwitchCompat>(R.id.search_switch)?.setOnCheckedChangeListener { _, state ->
            isChecked = state
            switchEvent.postValue(isChecked)
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        view<SwitchCompat>(R.id.search_switch)?.isChecked = isChecked
    }
}