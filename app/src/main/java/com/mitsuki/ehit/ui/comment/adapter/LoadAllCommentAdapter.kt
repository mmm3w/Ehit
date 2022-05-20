package com.mitsuki.ehit.ui.comment.adapter

import androidx.core.view.isVisible
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.databinding.ItemLoadAllCommentBinding

class LoadAllCommentAdapter(val action: () -> Unit) :
    SingleItemBindingAdapter<ItemLoadAllCommentBinding>(
        R.layout.item_load_all_comment,
        ItemLoadAllCommentBinding::bind,
        false
    ) {

    var loadState: LoadState = LoadState.Invisible
        set(value) {
            if (value != field) {
                if (value is LoadState.Invisible) {
                    isEnable = false
                } else {
                    if (field is LoadState.Invisible) {
                        isEnable = true
                    } else {
                        notifyItemChanged(0)
                    }
                }
                field = value
            }
        }

    override val onViewHolderCreate: ViewHolder<ItemLoadAllCommentBinding>.() -> Unit = {
        binding.loadAllText.apply {
            text = text(R.string.text_load_all_comment)
            setOnClickListener { action() }
        }
    }

    override val onViewHolderBind: ViewHolder<ItemLoadAllCommentBinding>.() -> Unit = {
        binding.loadAllText.isVisible = loadState is LoadState.LoadMore
        binding.loadAllProgress.isVisible = loadState is LoadState.Loading
    }

    sealed class LoadState {
        object Loading : LoadState()
        object LoadMore : LoadState()
        object Invisible : LoadState()
    }

}