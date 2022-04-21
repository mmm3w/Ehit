package com.mitsuki.ehit.ui.download.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ItemGalleryPreviewMenuOptionBinding

class DownloadListOptionAdapter : RecyclerView.Adapter<DownloadListOptionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.menuOptionText.text = string(R.string.text_refresh)
    }

    override fun getItemCount(): Int {
        return 1
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_preview_menu_option)) {
        val binding by viewBinding(ItemGalleryPreviewMenuOptionBinding::bind)
    }
}