package com.mitsuki.ehit.core.ui.adapter

import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.ehit.R

class SearchAdvancedAdapter : SingleItemAdapter(true) {

    var isVisible: Boolean = false
        set(value) {
            if (value != field) {
                if (value && !field && isEnable) {
                    notifyItemInserted(0)
                } else if (!value && field && isEnable) {
                    notifyItemRemoved(0)
                }
                field = value
            }
        }


    override var isEnable: Boolean = false
        set(value) {
            if (value != field) {
                if (value && !field && isVisible) {
                    notifyItemInserted(0)
                } else if (!value && field && isVisible) {
                    notifyItemRemoved(0)
                }
                field = value
            }
        }


    override fun getItemCount(): Int = if (isVisible && isEnable) 1 else 0

    override val layoutRes: Int = R.layout.item_search_advanced
    override val onViewHolderCreate: ViewHolder.() -> Unit = {

    }
    override val onViewHolderBind: ViewHolder.() -> Unit = {

    }

}