package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemGalleryPreviewMenuOptionBinding

class GalleryPreviewMenuAdapter : RecyclerView.Adapter<GalleryPreviewMenuAdapter.ViewHolder>() {

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