package com.mitsuki.ehit.ui.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ItemGalleryMenuOptionBinding

class BottomMenuAdapter(private val mOptions: IntArray) :
    RecyclerView.Adapter<BottomMenuAdapter.ViewHolder>(), EventEmitter {

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        post("option", position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.menuOptionText.text = string(mOptions[position])
    }

    override fun getItemCount(): Int {
        return mOptions.size
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_menu_option)) {
        val binding by viewBinding(ItemGalleryMenuOptionBinding::bind)
    }

    override val eventEmitter: Emitter = Emitter()
}