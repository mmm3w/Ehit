package com.mitsuki.ehit.ui.search.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ItemSearchQuickBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.GalleryDataMeta
import kotlin.collections.ArrayList

class QuickSearchAdapter : RecyclerView.Adapter<QuickSearchAdapter.ViewHolder>(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private val mData: NotifyQueueData<QuickSearch> = NotifyQueueData(Diff.QUICK_SEARCH)

    private var isAttached: Boolean = false

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        when (view.id) {
            R.id.quick_search_del -> post("delete", mData.item(position))
            else -> post("click", mData.item(position))
        }
    }

    private val mSortTouch = { view: View, event: MotionEvent ->
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            post("sort", view.tag as ViewHolder)
        }
        false
    }

    override fun getItemCount(): Int = mData.count

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
        holder.binding.quickSearchName.text = mData.item(position).name
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        isAttached = true
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        isAttached = false
    }

    /**********************************************************************************************/
    //一些快捷更新方法
    suspend fun submitData(data: List<QuickSearch>) {
        mData.postUpdate(NotifyData.Refresh(data))
    }

    suspend fun addItem(name: String, key: String, meta: GalleryDataMeta.Type) {
        if (name.isEmpty() || key.isEmpty()) return
        mData.postUpdate(NotifyData.Insert(QuickSearch(meta, name, key, 0)))
    }

    suspend fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        mData.postUpdate(NotifyData.Move(fromPosition, toPosition))
        return true
    }

    suspend fun removeItem(item: QuickSearch) {
        mData.postUpdate(NotifyData.Remove(item))
    }

    val newSortData: List<QuickSearch>
        get() {
            return ArrayList<QuickSearch>().apply {
                for (i in 0 until mData.count) {
                    add(mData.item(i).apply { sort = i + 1 })
                }
            }
        }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_search_quick)) {
        val binding by viewBinding(ItemSearchQuickBinding::bind)
    }
}