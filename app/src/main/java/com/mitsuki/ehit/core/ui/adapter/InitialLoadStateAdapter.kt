package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.InitialGate

class InitialLoadStateAdapter(private val adapter: PagingDataAdapter<*, *>) :
    RecyclerView.Adapter<InitialLoadStateAdapter.ViewHolder>() {

    private val mGate = InitialGate()

    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (mGate.ignore()) return

            if (field != loadState) {
                when (loadState) {
                    is LoadState.Loading -> mGate.prep(true)
                    is LoadState.Error -> mGate.prep(false)
                    is LoadState.NotLoading -> mGate.trigger()
                }

                val oldItem = displayLoadStateAsItem(field)
                val newItem = displayLoadStateAsItem(loadState)

                if (oldItem && !newItem) {
                    notifyItemRemoved(0)
                } else if (newItem && !oldItem) {
                    notifyItemInserted(0)
                } else if (oldItem && newItem) {
                    notifyItemChanged(0)
                }
                field = loadState
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent) { adapter.retry() }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(loadState)
    }

    override fun getItemCount(): Int {
        return if (displayLoadStateAsItem(loadState)) 1 else 0
    }

    class ViewHolder(parent: ViewGroup, private val retryCallback: () -> Unit) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.load_state_full, parent, false)
        ) {
        private val mRetryBtn = view<Button>(R.id.load_state_retry)?.apply {
            setOnClickListener { retryCallback() }
        }
        private val mProgressBar = view<ProgressBar>(R.id.load_state_progress)
        private val mError = view<TextView>(R.id.load_state_error)
        fun bindTo(loadState: LoadState) {
            mProgressBar?.isVisible = loadState is LoadState.Loading
            mRetryBtn?.isVisible = loadState is LoadState.Error
            mError?.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            mError?.text = (loadState as? LoadState.Error)?.error?.message
        }
    }

    private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }
}