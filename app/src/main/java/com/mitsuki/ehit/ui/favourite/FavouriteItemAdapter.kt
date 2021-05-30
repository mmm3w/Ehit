package com.mitsuki.ehit.ui.favourite

import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.string
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemFavouriteOptionBinding
import com.mitsuki.ehit.model.ehparser.GalleryFavorites

class FavouriteItemAdapter(var checkedItemIndex: Int = 0) :
    RecyclerView.Adapter<FavouriteItemAdapter.ViewHolder>() {

    private val mData by lazy {
        ArrayList(GalleryFavorites.favorites).apply {
            add(0, string(R.string.text_all))
        }
    }

    private var mCountData = Array(11) { 0 }

    val checkItem: SingleLiveEvent<Int> by lazy { SingleLiveEvent() }

    private val mItemCheckChange = { buttonView: CompoundButton, isChecked: Boolean ->
        if (buttonView.isPressed) {
            val position = (buttonView.tag as ViewHolder).bindingAdapterPosition
            if (isChecked && position != checkedItemIndex) {
                val olderChecked = checkedItemIndex
                checkedItemIndex = position
                notifyItemChanged(olderChecked)
                notifyItemChanged(checkedItemIndex)
                checkItem.postValue(checkedItemIndex)
            }
        }
    }

    override fun getItemCount(): Int = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            binding.favouriteOptionText.tag = this
            binding.favouriteOptionText.setOnCheckedChangeListener(mItemCheckChange)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.favouriteOptionText.text = mData[position]
        holder.binding.favouriteOptionText.isChecked = position == checkedItemIndex
        holder.binding.favouriteOptionCount.text =
            if (position >= mCountData.size) "0" else mCountData[position].toString()
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_favourite_option)) {
        val binding by viewBinding(ItemFavouriteOptionBinding::bind)
    }

    fun setExtendData(data: Array<Int>) {
        mCountData = data
        notifyDataSetChanged()
    }
}