package com.mitsuki.ehit.core.ui.adapter

import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R

class GalleryDetailInitialLoadStateAdapter(private val adapter: PagingDataAdapter<*, *>) :
    InitialLoadStateAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent) { adapter.retry() }

    class ViewHolder(parent: ViewGroup, private val retryCallback: () -> Unit) :
        InitialViewHolder(parent, R.layout.load_state_fit) {
        private val mRetryBtn = view<Button>(R.id.load_state_retry)?.apply {
            setOnClickListener { retryCallback() }
        }
        private val mProgressBar = view<ProgressBar>(R.id.load_state_progress)
        private val mError = view<TextView>(R.id.load_state_error)
        override fun bindTo(loadState: LoadState) {
            mProgressBar?.isVisible = loadState is LoadState.Loading
            mRetryBtn?.isVisible = loadState is LoadState.Error
            mError?.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            mError?.text = (loadState as? LoadState.Error)?.error?.message
        }
    }
}