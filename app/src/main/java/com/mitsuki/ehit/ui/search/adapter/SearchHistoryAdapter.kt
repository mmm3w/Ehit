package com.mitsuki.ehit.ui.search.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.calculateDiff
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemSearchBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.ui.search.SearchWordEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryAdapter : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    var isEnable: Boolean = true
        set(value) {
            if (value != field) {
                if (value && !field) {
                    notifyItemRangeInserted(0, mData.size)
                } else if (!value && field) {
                    notifyItemRangeRemoved(0, mData.size)
                }
                field = value
            }
        }

    private val mData: MutableList<SearchHistory> = arrayListOf()

    val clickItem: SingleLiveEvent<SearchWordEvent> by lazy { SingleLiveEvent() }

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition

        when (view.id) {
            R.id.search_item_delete -> clickItem.postValue(SearchWordEvent.Delete(mData[position]))
            else -> clickItem.postValue(SearchWordEvent.Select(mData[position]))
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
        return if (isEnable) mData.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    suspend fun submitData(data: List<SearchHistory>) {
        val result = withContext(Dispatchers.IO) {
            val result = calculateDiff(Diff.SEARCH_HISTORY, mData, data)
            mData.clear()
            mData.addAll(data)
            result
        }

        withContext(Dispatchers.Main) {
            result.dispatchUpdatesTo(this@SearchHistoryAdapter)
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