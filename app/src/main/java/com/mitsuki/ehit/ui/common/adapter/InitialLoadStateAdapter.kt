package com.mitsuki.ehit.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.extensions.viewBinding

abstract class InitialLoadStateAdapter<VB : ViewBinding> :
    RecyclerView.Adapter<InitialViewHolder<VB>>() {

    private val mGate = InitialGate()

    val isOver get() = mGate.ignore()

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

    override fun onBindViewHolder(holder: InitialViewHolder<VB>, position: Int) {
        holder.bindTo(loadState)
    }

    override fun getItemCount(): Int {
        return if (displayLoadStateAsItem(loadState)) 1 else 0
    }

    private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }
}

abstract class InitialViewHolder<VB : ViewBinding>(
    parent: ViewGroup,
    @LayoutRes layout: Int,
    bind: (View) -> VB
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layout, parent, false)
    ) {

    val binding by viewBinding(bind)
    abstract fun bindTo(loadState: LoadState)
}