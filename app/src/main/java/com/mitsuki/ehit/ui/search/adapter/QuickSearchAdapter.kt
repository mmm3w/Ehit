package com.mitsuki.ehit.ui.search.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.calculateDiff
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.Log
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemSearchQuickBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.QuickSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*

class QuickSearchAdapter : RecyclerView.Adapter<QuickSearchAdapter.ViewHolder>() {

    private val mData: MutableList<QuickSearch> = arrayListOf()

    val clickItem by lazy { SingleLiveEvent<Event>() }
    val sortDragTrigger by lazy { SingleLiveEvent<ViewHolder>() }

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        when (view.id) {
            R.id.quick_search_del -> {
                clickItem.postValue(Event.Delete(mData.removeAt(position)))
                notifyItemRemoved(position)
            }
            else -> clickItem.postValue(Event.Click(mData[position]))
        }
    }

    private val mSortTouch = { view: View, event: MotionEvent ->
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            sortDragTrigger.postValue(view.tag as ViewHolder)
        }
        false
    }

    override fun getItemCount(): Int = mData.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
            binding.quickSearchDel.tag = this
            binding.quickSearchDel.setOnClickListener(mItemClick)
            binding.quickSearchSort.tag = this
            binding.quickSearchSort.setOnTouchListener(mSortTouch)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.quickSearchName.text = mData[position].name
    }

    suspend fun submitData(data: List<QuickSearch>) {
        withContext(Dispatchers.Main) {
            //请在主线程中操作数据？
            //否则RV会乱七八糟。。
            //技术太菜还在寻求方案中
            if (data.isEmpty() && mData.isEmpty()) return@withContext
            if (data.isEmpty() && mData.isNotEmpty()) {
                val size = mData.size
                mData.clear()
                notifyItemRangeRemoved(0, size)
                return@withContext
            }

            if (data.isNotEmpty() && mData.isEmpty()) {
                mData.addAll(data)
                notifyItemRangeInserted(0, mData.size)
                return@withContext
            }

            val result =
                withContext(Dispatchers.IO) { calculateDiff(Diff.QUICK_SEARCH, mData, data) }

            mData.clear()
            mData.addAll(data)
            result.dispatchUpdatesTo(this@QuickSearchAdapter)
        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mData, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mData, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    val newSortData: List<QuickSearch>
        get() {
            return mData.mapIndexed { i: Int, quickSearch: QuickSearch ->
                quickSearch.apply { sort = i + 1 }
            }
        }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_search_quick)) {
        val binding by viewBinding(ItemSearchQuickBinding::bind)
    }

    sealed class Event(val data: QuickSearch) {
        class Click(data: QuickSearch) : Event(data)
        class Delete(data: QuickSearch) : Event(data)
    }
}