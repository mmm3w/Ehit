package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.LoadStateFitBinding
import com.mitsuki.ehit.ui.common.adapter.BindingViewHolder
import com.mitsuki.ehit.ui.common.adapter.DefaultLoadStateAdapter

class GalleryDetailLoadStatesAdapter(private val retryAction: () -> Unit) :
    LoadStateAdapter<GalleryDetailLoadStatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(parent).apply {
            binding.loadStateRetry.setOnClickListener { retryAction() }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        with(holder.binding) {
            loadStateProgress.isVisible = loadState is LoadState.Loading
            loadStateRetry.isVisible = loadState is LoadState.Error
            loadStateError.apply {
                isVisible = loadState is LoadState.Error
                text = (loadState as? LoadState.Error)?.error?.message ?: ""
            }
        }
    }

    class ViewHolder(parent: ViewGroup) : BindingViewHolder<LoadStateFitBinding>(
        parent,
        R.layout.load_state_fit,
        LoadStateFitBinding::bind
    )
}