package com.mitsuki.ehit.ui.widget

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.addOnScrollListenerBy
import com.mitsuki.armory.extend.marginVertical
import com.mitsuki.ehit.R

@Suppress("JoinDeclarationAndAssignment", "ConstantConditionIf")
class GalleryListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingChild3 {

    private var mRecyclerView: RecyclerView //数据列表

    private val mFabSlop: Int

    private val mFloatBarPlugin: FloatBarPlugin

    private var mExtendControl: ((toHide: Boolean) -> Unit)? = null
    private var mExtendTag = false

    init {
        mFabSlop = ViewConfiguration.get(context).scaledTouchSlop
        //创建 RecyclerView
        mRecyclerView = RecyclerView(context).apply {
            id = R.id.gallery_list_inner_recyclerview
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        addView(mRecyclerView)

        mFloatBarPlugin =
            FloatBarPlugin(context, R.layout.top_bar_main_search, this).apply { addView(view()) }

        mRecyclerView.addOnScrollListener(mFloatBarPlugin)
        mRecyclerView.addOnScrollListenerBy(
            onScrolled = { _, _, dy: Int ->
                if (dy >= mFabSlop) {
                    //收缩
                    if (mExtendTag) {
                        mExtendControl?.invoke(true)
                        mExtendTag = false
                    }
                } else if (dy <= -mFabSlop / 2) {
                    //展开
                    if (!mExtendTag) {
                        mExtendControl?.invoke(false)
                        mExtendTag = true
                    }
                }
            })
    }

    private val mScrollingChildHelper by lazy { NestedScrollingChildHelper(mRecyclerView) }

    @Suppress("unused", "PropertyName")
    class SavedState : BaseSavedState {
        val topBarOffset: Float

        constructor(superState: Parcelable?, offset: Float) : super(superState) {
            this.topBarOffset = offset
        }

        constructor(source: Parcel?) : super(source) {
            topBarOffset = source?.readFloat() ?: 0f
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeFloat(topBarOffset)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, mFloatBarPlugin.offset)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as? SavedState
        super.onRestoreInstanceState(savedState?.superState)
        mFloatBarPlugin.offset = savedState?.topBarOffset ?: 0f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mFloatBarPlugin.view().measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )

        mRecyclerView.setPadding(
            0, mFloatBarPlugin.view().measuredHeight + mFloatBarPlugin.view().marginVertical(),
            0, 0
        )

        mRecyclerView.measure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mFloatBarPlugin.view { layout(0, 0, measuredWidth, measuredHeight) }

        mRecyclerView.apply { layout(0, 0, measuredWidth, measuredHeight) }

    }


    /** nested scroll *****************************************************************************/
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mScrollingChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        mScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mScrollingChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        mScrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mScrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    /**********************************************************************************************/
    fun recyclerView(action: (RecyclerView.() -> Unit)? = null) =
        action?.run { mRecyclerView.apply(this) } ?: mRecyclerView

    fun topBar(action: View.() -> Unit) = mFloatBarPlugin.view(action)

    fun setListener(
        extendControl: ((toHide: Boolean) -> Unit)? = null
    ) {
        this.mExtendControl = extendControl
    }


}