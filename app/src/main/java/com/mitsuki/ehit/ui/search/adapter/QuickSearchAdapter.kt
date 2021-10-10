package com.mitsuki.ehit.ui.search.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.AttachedAnchor
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemSearchQuickBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.page.GalleryPageSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class QuickSearchAdapter : RecyclerView.Adapter<QuickSearchAdapter.ViewHolder>() {

    private val mData: MutableList<QuickSearch> = arrayListOf()
    private val pendingUpdates: ArrayDeque<NotifyData<QuickSearch>> = ArrayDeque()

    private val mClickEvent: PublishSubject<Event> = PublishSubject.create()
    private val mSortDragTriggerEvent: PublishSubject<ViewHolder> = PublishSubject.create()

    val clickItem: Observable<Event> get() = mClickEvent.hide()
    val sortDragTrigger: Observable<ViewHolder> get() = mSortDragTriggerEvent.hide()

    private var isAttached: Boolean = false

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        when (view.id) {
            R.id.quick_search_del -> mClickEvent.onNext(Event.Delete(mData[position]))
            else -> mClickEvent.onNext(Event.Click(mData[position]))
        }
    }

    private val mSortTouch = { view: View, event: MotionEvent ->
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            mSortDragTriggerEvent.onNext(view.tag as ViewHolder)
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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        isAttached = true
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        isAttached = false
    }

    /**********************************************************************************************/
    //协程队列更新
    private suspend fun postUpdate(data: NotifyData<QuickSearch>) {
        if (!isAttached) {
            data.directUpdate(mData)
            return
        }

        pendingUpdates.add(data)
        if (pendingUpdates.size > 1) return
        updateData(data)
    }

    private suspend fun updateData(data: NotifyData<QuickSearch>) {
        withContext(Dispatchers.Main) {
            when (data) {
                is NotifyData.Insert,
                is NotifyData.RangeInsert,
                is NotifyData.RemoveAt,
                is NotifyData.Remove,
                is NotifyData.Change,
                is NotifyData.Clear,
                is NotifyData.Move,
                is NotifyData.ChangeAt -> applyNotify(data)
                is NotifyData.RangeRemove,
                is NotifyData.ChangeIf,
                is NotifyData.Refresh -> {
                    withContext(Dispatchers.IO) { data.calculateDiff(mData, Diff.QUICK_SEARCH) }
                    applyNotify(data)
                }
            }
        }
    }

    private suspend fun applyNotify(notifyData: NotifyData<QuickSearch>) {
        pendingUpdates.remove()
        notifyData.dispatchUpdates(mData, this)
        if (pendingUpdates.isNotEmpty()) {
            pendingUpdates.peek()?.apply { updateData(this) }
        }
    }

    /**********************************************************************************************/
    //一些快捷更新方法
    suspend fun submitData(data: List<QuickSearch>) {
        withContext(Dispatchers.IO) {
            if (data.isEmpty() && mData.isEmpty()) return@withContext
            if (data.isEmpty() && mData.isNotEmpty()) {
                postUpdate(NotifyData.Clear())
                return@withContext
            }
            if (data.isNotEmpty() && mData.isEmpty()) {
                postUpdate(NotifyData.RangeInsert(data))
                return@withContext
            }
            postUpdate(NotifyData.Refresh(data))
        }
    }

    suspend fun addItem(name: String, key: String, type: GalleryPageSource.Type) {
        if (name.isEmpty() || key.isEmpty()) return
        postUpdate(NotifyData.Insert(QuickSearch(type, name, key, 0)))
    }

    suspend fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        postUpdate(NotifyData.Move(fromPosition, toPosition))
        return true
    }

    suspend fun removeItem(item: QuickSearch) {
        postUpdate(NotifyData.Remove(item))
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