package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.LoadStateFitBinding
import com.mitsuki.ehit.ui.common.adapter.InitialLoadStateAdapter
import com.mitsuki.ehit.ui.common.adapter.InitialViewHolder

class GalleryDetailInitialLoadStateAdapter(private val adapter: PagingDataAdapter<*, *>) :
    InitialLoadStateAdapter<LoadStateFitBinding>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent) { adapter.retry() }

    class ViewHolder(parent: ViewGroup, private val retryCallback: () -> Unit) :
        InitialViewHolder<LoadStateFitBinding>(
            parent,
            R.layout.load_state_fit,
            LoadStateFitBinding::bind
        ) {

        init {
            binding.loadStateRetry.setOnClickListener { retryCallback() }
        }

        override fun bindTo(loadState: LoadState) {
            binding.loadStateProgress.isVisible = loadState is LoadState.Loading
            binding.loadStateRetry.isVisible = loadState is LoadState.Error
            binding.loadStateError.isVisible =
                !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            binding.loadStateError.text = (loadState as? LoadState.Error)?.error?.message
        }
    }
}