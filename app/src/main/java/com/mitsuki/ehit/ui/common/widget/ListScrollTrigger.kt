package com.mitsuki.ehit.ui.common.widget

import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

class ListScrollTrigger(
    view: RecyclerView,
    private val func: (trigger: Boolean) -> Unit
) :
    RecyclerView.OnScrollListener() {

    private val mFabSlop = ViewConfiguration.get(view.context).scaledTouchSlop
    private var mTriggerTag = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy >= mFabSlop) {
            if (mTriggerTag) {
                mTriggerTag = false
                func.invoke(true)
            }
        } else if (dy <= -mFabSlop / 2) {
            if (!mTriggerTag) {
                mTriggerTag = true
                func.invoke(false)
            }
        }
    }
}