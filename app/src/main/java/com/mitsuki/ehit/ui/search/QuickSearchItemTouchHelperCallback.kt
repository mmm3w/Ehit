package com.mitsuki.ehit.ui.search

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.SingleLiveEvent

class QuickSearchItemTouchHelperCallback : ItemTouchHelper.Callback() {

    val swapEvent: SingleLiveEvent<Pair<Int, Int>> by lazy { SingleLiveEvent() }
    val dataSwap: SingleLiveEvent<Pair<Int, Int>> by lazy { SingleLiveEvent() }

    private var lastSwap: Pair<Int, Int>? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        lastSwap = fromPosition to toPosition
        swapEvent.postValue(lastSwap)
        return true
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder?.itemView?.apply {
                    //为该tag设置0
                    //1、可防止内部实现修改自行设置的elevation
                    //2、不用再重写clearView来处理elevation，内部实现会在clearView方法中自动读取该tag
                    setTag(R.id.item_touch_helper_previous_elevation, 0f)
                    ViewCompat.setElevation(this, dp2px(8f))
                }
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                lastSwap?.apply { dataSwap.postValue(this) }
                lastSwap = null
            }
        }
    }
}