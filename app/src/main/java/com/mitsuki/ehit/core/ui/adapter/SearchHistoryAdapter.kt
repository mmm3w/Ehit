package com.mitsuki.ehit.core.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.calculateDiff
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.SearchHistory
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

    private val currentItem: MutableLiveData<String> = MutableLiveData()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        currentItem.postValue(mData[holder.bindingAdapterPosition].text)
    }

    val itemClickEvent: LiveData<String>
        get() = currentItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun getItemCount(): Int {
        return if (isEnable) mData.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, mData[position])
    }

    suspend fun submitData(data: List<SearchHistory>) {
        val result = withContext(Dispatchers.IO) {
            val result = calculateDiff(SearchHistory.DIFF, mData, data)
            mData.clear()
            mData.addAll(data)
            result
        }

        withContext(Dispatchers.Main) {
            result.dispatchUpdatesTo(this@SearchHistoryAdapter)
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        ) {

        private val mSearchIcon = view<ImageView>(R.id.search_item_icon)
        private val mSearchText = view<TextView>(R.id.search_item_text)


        fun bind(index: Int, item: SearchHistory) {
            mSearchIcon?.setImageResource(R.drawable.ic_round_history_24)
            mSearchText?.text = item.text
        }
    }


}