package com.mitsuki.ehit.ui.search.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R

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

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        itemView.setOnClickListener(mClickEvent)
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        view<ImageView>(R.id.search_item_icon)
            ?.setImageResource(if (isAdvancedMode) R.drawable.ic_round_keyboard_return_24 else R.drawable.ic_outline_change_circle_24)
        view<TextView>(R.id.search_item_text)?.apply {
            text =
                if (isAdvancedMode)
                    context.getText(R.string.text_quick_search)
                else
                    context.getText(R.string.text_advanced_search)
        }
    }

}