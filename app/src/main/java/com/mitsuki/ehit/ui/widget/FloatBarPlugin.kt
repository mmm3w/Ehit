package com.mitsuki.ehit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.statusBarHeight

class FloatBarPlugin(context: Context, @LayoutRes val layoutRes: Int, root: ViewGroup? = null) :
    RecyclerView.OnScrollListener() {

    private var mOffsetLimit: Float = -1f
    private val mStatusBarHeight: Int = context.statusBarHeight()
    private val mView: View = LayoutInflater.from(context).inflate(layoutRes, root, false)


    /**********************************************************************************************/
    fun view(action: (View.() -> Unit)? = null) = action?.run { mView.apply(action) } ?: mView

    var offset: Float
        set(value) {
            mView.translationY = value
        }
        get() = mView.translationY


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        move(dy)
    }

    private fun move(dy: Int) {
        if (mOffsetLimit < 0) {
            mOffsetLimit = mView.measuredHeight.toFloat()
        }

        if (mOffsetLimit == 0f) return

        if (dy > 0) {
            if (mView.translationY > -mOffsetLimit) {
                if (mView.translationY - dy < -mOffsetLimit) {
                    mView.translationY = -mOffsetLimit
                } else {
                    mView.translationY -= dy
                }
            }
        }

        if (dy < 0) {
            if (mView.translationY < 0) {
                if (mView.translationY - dy > 0) {
                    mView.translationY = 0f
                } else {
                    mView.translationY -= dy
                }
            }
        }
    }

}