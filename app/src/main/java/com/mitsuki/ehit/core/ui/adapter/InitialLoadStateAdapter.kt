package com.mitsuki.ehit.being

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.core.crutch.InitialGate

abstract class InitialLoadStateAdapter() :
    RecyclerView.Adapter<InitialViewHolder>() {

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

    override fun onBindViewHolder(holder: InitialViewHolder, position: Int) {
        holder.bindTo(loadState)
    }

    override fun getItemCount(): Int {
        return if (displayLoadStateAsItem(loadState)) 1 else 0
    }

    private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }
}

abstract class InitialViewHolder(parent: ViewGroup, @LayoutRes layout: Int) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layout, parent, false)
    ) {
    abstract fun bindTo(loadState: LoadState)
}