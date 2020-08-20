package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.mitsuki.armory.drawable.TricolorBarDrawable
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.ehit.R

class LoadPlugin(context: Context) {

    private var mLoading = false
    private var mEnableLoad = true
    private var mLoadRange: Float //加载上拉换算
    private var mCurrentLoadDragY = 0f //加载用上拉距离

    private val mLoadView: ImageView =
        ImageView(context).apply { id = R.id.gallery_list_inner_load_view }//加载View
    private val mLoadTricolorBar: TricolorBarDrawable = TricolorBarDrawable()//加载View中的进度条

    companion object {
        private const val LOAD_RANGE = 200
    }

    init {
        context.resources.displayMetrics.density.apply {
            mLoadRange = dp2px(LOAD_RANGE.toFloat()).toFloat()
        }

        mLoadView.setImageDrawable(mLoadTricolorBar)
    }

    /**********************************************************************************************/
    fun view(action: (ImageView.() -> Unit)? = null) =
        action?.run { mLoadView.apply(action) } ?: mLoadView

    val inOperation: Boolean
        get() = mCurrentLoadDragY > 0 || mLoading

    @Suppress("SpellCheckingInspection")
    val loadTriggerable: Boolean
        get() = !mLoading && mEnableLoad

    var loadListener: (() -> Unit)? = null

    //额外条件
    var additional: ((dy: Int) -> Boolean)? = null

    var isLoading: Boolean
        set(value) {
            if (mLoading == value) return
            mLoading = value
            if (mLoading) {
                mLoadTricolorBar.start()
            } else {
                mLoadTricolorBar.stop()
            }
        }
        get() = mLoading

    fun startDrag() {
        mCurrentLoadDragY = 0f
    }

    fun drag(dy: Int): Int {
        if (dy > 0) {
            //三个条件：1、不可上拉。2、加载可触发。3、刷新View未被拉下来
            if (loadTriggerable) {
                if (additional(dy)) {
                    mCurrentLoadDragY += dy
                    extendLoad()
                    return dy
                } else {
                    if (mCurrentLoadDragY > 0) {
                        mCurrentLoadDragY = 0f
                        mLoadTricolorBar.back()
                    }
                }
            }
        }

        if (dy < 0) {
            //两个条件：1、上拉是可用。2、load以延展
            if (loadTriggerable) {
                if (additional(dy)) {
                    if (mCurrentLoadDragY > 0) {
                        mCurrentLoadDragY += dy
                        extendLoad()
                        return dy
                    }
                } else {
                    if (mCurrentLoadDragY > 0) {
                        mCurrentLoadDragY = 0f
                        mLoadTricolorBar.back()
                    }
                }
            }
        }

        return 0
    }

    fun finishDrag() {
        if (mCurrentLoadDragY > 0) {
            triggerLoad()
            mCurrentLoadDragY = 0f
        }
    }

    /**********************************************************************************************/
    private fun extendLoad() {
        mLoadTricolorBar.setProgress(mCurrentLoadDragY / mLoadRange)
    }

    private fun triggerLoad() {
        if (mCurrentLoadDragY >= mLoadRange) {
            if (!mLoading) {
                mLoading = true
                mLoadTricolorBar.start()
                loadListener?.invoke()
            }
        } else {
            mLoadTricolorBar.back()
        }
    }

    private fun additional(dy: Int) = additional?.invoke(dy) ?: true

}