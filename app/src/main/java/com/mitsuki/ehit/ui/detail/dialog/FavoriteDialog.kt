package com.mitsuki.ehit.ui.detail.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.DialogDetailFavouriteBinding
import com.mitsuki.ehit.databinding.ItemFavouriteBinding
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.ui.common.dialog.BaseDialogFragment

class FavoriteDialog(
    private val initName: String?,
    private val onSelected: (Int) -> Unit
) :
    BaseDialogFragment(R.layout.dialog_detail_favourite) {

    private val binding by viewBinding(DialogDetailFavouriteBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mAdapter = FavoriteAdapter(GalleryFavorites.findIndex(initName)){
            onSelected(it)
            dismiss()
        }
        binding?.dialogFavouriteList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    class FavoriteAdapter(s: Int, private val onSelected: (Int) -> Unit) :
        RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

        private val mData = GalleryFavorites.favorites

        var selected: Int = s
            private set(value) {
                if (value != field) {
                    val last = field
                    field = value
                    notifyItemChanged(last)
                    onSelected.invoke(field)
                }
            }

        private val mItemClick =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked)
                    selected = (buttonView?.tag as? ViewHolder)?.bindingAdapterPosition ?: -1
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent).apply {
                binding.favouriteOptionText.tag = this
                binding.favouriteOptionText.setOnCheckedChangeListener(mItemClick)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.favouriteOptionText.text = mData[position]
            holder.binding.favouriteOptionText.isChecked = position == selected
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        class ViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.createItemView(R.layout.item_favourite)) {
            val binding by viewBinding(ItemFavouriteBinding::bind)
        }
    }
}