package com.mitsuki.ehit.ui.download.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.ItemDownloadControlBinding

class ControlAdapter : SingleItemBindingAdapter<ItemDownloadControlBinding>(
    R.layout.item_download_control,
    ItemDownloadControlBinding::bind
) {
    override val onViewHolderBind: ViewHolder<ItemDownloadControlBinding>.() -> Unit = {

    }
}