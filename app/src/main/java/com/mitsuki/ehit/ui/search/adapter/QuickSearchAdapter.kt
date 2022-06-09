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
import com.mitsuki.ehit.ui.common.adapter.BindingViewHolder
import kotlin.collections.ArrayList

class QuickSearchAdapter(private val mData: NotifyQueueData<QuickSearch>) :
    RecyclerView.Adapter<QuickSearchAdapter.ViewHolder>(), EventEmitter {

    init {
        mData.attachAdapter(this)
    }

    override val eventEmitter: Emitter = Emitter()

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

    class ViewHolder(parent: ViewGroup) :
        BindingViewHolder<ItemSearchQuickBinding>(
            parent,
            R.layout.item_search_quick,
            ItemSearchQuickBinding::bind
        )
}