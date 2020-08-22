package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.ehit.R

class DefaultLoadStateAdapter(private val adapter: PagingDataAdapter<*, *>) :
    LoadStateAdapter<DefaultLoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(parent) { adapter.retry() }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bindTo(loadState)
    }

    class ViewHolder(parent: ViewGroup, private val retryCallback: () -> Unit) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.load_state_default, parent, false)
        ) {
        private val mRetryBtn = view<Button>(R.id.load_state_retry)?.apply {
            setOnClickListener { retryCallback() }
        }
        private val mProgressBar = view<ProgressBar>(R.id.load_state_progress)
        fun bindTo(loadState: LoadState) {
            mProgressBar?.isVisible = loadState is LoadState.Loading
            mRetryBtn?.isVisible = loadState is LoadState.Error
        }
    }
}