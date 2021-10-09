package com.mitsuki.ehit.ui.search.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemSearchBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.ui.search.SearchWordEvent

class SearchHistoryAdapter : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>(), EventEmitter {

    var isEnable: Boolean = true
        set(value) {
            if (value != field) {
                if (value && !field) {
                    notifyItemRangeInserted(0, mData.count)
                } else if (!value && field) {
                    notifyItemRangeRemoved(0, mData.count)
                }
                field = value
            }
        }

    override val eventEmitter: Emitter = Emitter()

    private val mData: NotifyQueueData<SearchHistory> = NotifyQueueData(Diff.SEARCH_HISTORY).apply {
        attachAdapter(this@SearchHistoryAdapter)
    }

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition

        when (view.id) {
            R.id.search_item_delete -> post("his", SearchWordEvent.Delete(mData.item(position)))
            else -> post("his", SearchWordEvent.Select(mData.item(position)))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
            binding.searchItemDelete.tag = this
            binding.searchItemDelete.setOnClickListener(mItemClick)
        }
    }

    override fun getItemCount(): Int {
        return if (isEnable) mData.count else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData.item(position))
    }

    suspend fun submitData(data: List<SearchHistory>) {
        when {
            data.isEmpty() && mData.count > 0 ->
                mData.postUpdate(NotifyData.Clear())
            data.isNotEmpty() && mData.count == 0 ->
                mData.postUpdate(NotifyData.RangeInsert(data))
            data.isEmpty() && mData.count == 0 -> {

            }
            else -> {
                mData.postUpdate(NotifyData.Refresh(data))
            }
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_search)) {

        val binding by viewBinding(ItemSearchBinding::bind)

        init {
            binding.searchItemIcon.setImageResource(R.drawable.ic_round_history_24)
        }

        fun bind(item: SearchHistory) {
            binding.searchItemText.text = item.text
        }
    }
}