package com.mitsuki.ehit.core.ui.adapter

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R

class SearchExpandMore : SingleItemAdapter(false) {

    val expendEvent: MutableLiveData<Boolean> = MutableLiveData()

    var isExpand: Boolean = false
        private set(value) {
            if (value != field) {
                if (isEnable) notifyItemChanged(0)
                field = value
            }
        }

    private val mClickTrigger = { _: View ->
        trigger()
        expendEvent.postValue(isExpand)
    }

    private fun trigger() {
        isExpand = !isExpand
    }

    override val layoutRes: Int = R.layout.item_search_expand

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        itemView.setOnClickListener(mClickTrigger)
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        view<ImageView>(R.id.search_expand)?.setImageResource(
            if (isExpand) R.drawable.ic_round_expand_less_24 else R.drawable.ic_round_expand_more_24
        )
    }
}