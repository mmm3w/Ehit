package com.mitsuki.ehit.ui.detail.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DetailOperatingLayoutManager(context: Context) : LinearLayoutManager(context) {
    init {
        orientation = HORIZONTAL
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.run { detachAndScrapAttachedViews(this) }
        if (itemCount <= 0 || state?.isPreLayout!!) return
        for (i in 0 until itemCount) {
            recycler?.getViewForPosition(i)?.let {
                addView(it)
                measureChildWithMargins(it, 0, 0)
                val newWidth = width / itemCount
                it.layoutParams.width = newWidth
                layoutDecorated(
                    it,
                    i * newWidth,
                    0,
                    i * newWidth + newWidth,
                    getDecoratedMeasuredHeight(it)
                )
            }
        }
    }
}