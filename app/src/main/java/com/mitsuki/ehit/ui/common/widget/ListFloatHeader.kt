package com.mitsuki.ehit.ui.common.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.base.extend.marginVertical
import com.mitsuki.armory.base.extend.paddingVertical

class ListFloatHeader(private val mView: View, private val stateBack: (Float) -> Unit) :
    RecyclerView.OnScrollListener() {

    private var mOffsetLimit: Float = -1f


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        move(dy)
    }

    private fun move(dy: Int) {
        if (mOffsetLimit < 0) {
            mOffsetLimit =
                mView.measuredHeight.toFloat() + mView.paddingVertical() + mView.marginVertical()
        }

        if (mOffsetLimit == 0f) return

        if (dy > 0) {
            if (mView.translationY > -mOffsetLimit) {
                if (mView.translationY - dy < -mOffsetLimit) {
                    mView.translationY = -mOffsetLimit
                } else {
                    mView.translationY -= dy
                }
                stateBack(mView.translationY)
            }
        }

        if (dy < 0) {
            if (mView.translationY < 0) {
                if (mView.translationY - dy > 0) {
                    mView.translationY = 0f
                } else {
                    mView.translationY -= dy
                }
                stateBack(mView.translationY)
            }
        }
    }
}