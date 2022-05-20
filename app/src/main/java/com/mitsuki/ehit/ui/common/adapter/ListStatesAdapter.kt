package com.mitsuki.ehit.ui.common.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.databinding.LoadStateFullBinding

/**
 * empty、error、init、load
 */
class ListStatesAdapter(private val action: (() -> Unit)? = null) :
    RecyclerView.Adapter<ListStatesAdapter.ViewHolder>() {

    @Suppress("LiftReturnOrAssignment")
    var listState: ListState = ListState.None
        set(value) {
            if (value != field) {
                if (field is ListState.None) {
                    if (!isRefreshEnable && value is ListState.Refresh) {
                        /* do nothing*/
                    } else {
                        notifyItemInserted(0)
                        field = value
                    }
                } else {
                    if (value is ListState.None) {
                        notifyItemRemoved(0)
                        field = value
                    } else {
                        if (!isRefreshEnable && value is ListState.Refresh) {
                            /* do nothing*/
//                            notifyItemRemoved(0)
//                            field = ListState.None
                        } else {
                            notifyItemChanged(0)
                            field = value
                        }
                    }
                }
            }
        }


    var isRefreshEnable = true
        set(value) {
            if (value != field) {
                if (!value && listState is ListState.Refresh) {
                    listState = ListState.None
                }
                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            binding.loadStateRetry.setOnClickListener { action?.invoke() }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.loadStateProgress.isVisible = listState is ListState.Refresh
        holder.binding.loadStateRetry.isVisible = listState is ListState.Error
        when (listState) {
            is ListState.Error -> {
                holder.binding.loadStateText.isVisible = true
                holder.binding.loadStateText.text = (listState as ListState.Error).error.message
            }
            is ListState.Message -> {
                holder.binding.loadStateText.isVisible = true
                holder.binding.loadStateText.text = (listState as ListState.Message).text
            }
            else -> {
                holder.binding.loadStateText.isVisible = false
            }
        }
    }

    override fun getItemCount(): Int {
        return if (listState is ListState.None) 0 else 1
    }

    class ViewHolder(parent: ViewGroup) :
        BindingViewHolder<LoadStateFullBinding>(
            parent,
            R.layout.load_state_full,
            LoadStateFullBinding::bind
        )

    sealed class ListState {
        class Error(val error: Throwable) : ListState()

        class Message(val text: CharSequence) : ListState() {
            override fun equals(other: Any?): Boolean {
                return other is Message && other.text == text
            }

            override fun hashCode(): Int {
                return text.hashCode()
            }
        }

        object None : ListState()
        object Refresh : ListState()
    }
}