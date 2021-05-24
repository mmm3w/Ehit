package com.mitsuki.ehit.ui.adapter

import android.media.Image
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
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.SearchHistory
import com.mitsuki.ehit.ui.activity.SearchWordEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
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

    private val mEventSubject = PublishSubject.create<SearchWordEvent>()

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        when (view.id) {
            R.id.search_item_delete -> mEventSubject.onNext(SearchWordEvent.Delete(mData[position], 0))
            else -> mEventSubject.onNext(SearchWordEvent.Select(mData[position]))
        }
    }

    private val mItemLongClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        mEventSubject.onNext(SearchWordEvent.Mark(mData[position]))
        true
    }

    val itemEvent: Observable<SearchWordEvent>
        get() = mEventSubject.hideWithMainThread()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
            itemView.setOnLongClickListener(mItemLongClick)
            mSearchDelete?.tag = this
            mSearchDelete?.setOnClickListener(mItemClick)
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
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        ) {

        private val mSearchIcon = view<ImageView>(R.id.search_item_icon)
        private val mSearchText = view<TextView>(R.id.search_item_text)
        val mSearchDelete = view<ImageView>(R.id.search_item_delete)

        fun bind(index: Int, item: SearchHistory) {
            mSearchIcon?.setImageResource(R.drawable.ic_round_history_24)
            mSearchText?.text = item.text
        }
    }
}