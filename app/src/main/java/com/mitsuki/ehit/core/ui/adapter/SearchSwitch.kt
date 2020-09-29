package com.mitsuki.ehit.core.ui.adapter

import android.view.View
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.ehit.R

class SearchSwitch : SingleItemAdapter(true) {



    private val mClickEvent = {_: View ->

    }

    override val layoutRes: Int = R.layout.item_search_switch

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        itemView.setOnClickListener(mClickEvent)
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {}
}