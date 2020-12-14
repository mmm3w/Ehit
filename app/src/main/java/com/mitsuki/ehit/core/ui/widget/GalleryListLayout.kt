package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.*
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.InitialGate
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("JoinDeclarationAndAssignment", "ConstantConditionIf")
class GalleryListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent3 {

    private var mRecyclerView: RecyclerView //数据列表
    private val marginSize: Int

    //基础配置参数
    private val mCircleDiameter: Int //刷新View的大小
    private val mFabSlop: Int

    private val mRefreshPlugin: RefreshPlugin
    private val mRefreshGate = InitialGate()

    private val mFloatBarPlugin: FloatBarPlugin

    private var mExtendControl: ((toHide: Boolean) -> Unit)? = null
    private var mExtendTag = false

    companion object {
        private const val CIRCLE_DIAMETER = 40
    }

    init {
        resources.displayMetrics.density.apply {
            mCircleDiameter = (CIRCLE_DIAMETER * this).roundToInt()
            marginSize = dp2px(36f)
        }
        mFabSlop = ViewConfiguration.get(context).scaledTouchSlop
        //创建 RecyclerView
        mRecyclerView = RecyclerView(context).apply {
            id = R.id.gallery_list_inner_recyclerview
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        addView(mRecyclerView)

        //刷新View
        mRefreshPlugin = RefreshPlugin(context).apply { addView(view()) }

        //创建top bar
        mFloatBarPlugin =
            FloatBarPlugin(context, R.layout.part_top_search_bar, this).apply { addView(view()) }

        mRefreshPlugin.additional = {
            if (!endOfPrepend)
                false
            else
                if (it > 0)
                    true
                else
                    !mRecyclerView.canScrollVertically(-1)
        }

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
        mRefreshPlugin.view {
            measure(
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY)
            )
        }

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mFloatBarPlugin.view { layout(0, 0, measuredWidth, measuredHeight) }

        mRefreshPlugin.view {
            layout(
                this@GalleryListLayout.measuredWidth / 2 - measuredWidth / 2,
                mFloatBarPlugin.view().measuredHeight,
                this@GalleryListLayout.measuredWidth / 2 + measuredWidth / 2,
                measuredHeight + mFloatBarPlugin.view().measuredHeight
            )
        }

        mRecyclerView.apply { layout(0, 0, measuredWidth, measuredHeight) }

    }


    /** nested scroll *****************************************************************************/
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            mRefreshPlugin.startDrag()
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {

        if (type != ViewCompat.TYPE_TOUCH) return

        var temp = 0
        mRefreshPlugin.drag(dy).apply {
            if (abs(this) > temp) temp = this
        }
        consumed[1] = temp

    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        type: Int, consumed: IntArray
    ) {
    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, type: Int
    ) {
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            mRefreshPlugin.finishDrag()
        }
    }

    /**********************************************************************************************/
    fun recyclerView(action: (RecyclerView.() -> Unit)? = null) =
        action?.run { mRecyclerView.apply(this) } ?: mRecyclerView

    fun topBar(action: View.() -> Unit) = mFloatBarPlugin.view(action)

    fun setListener(
        refreshListener: (() -> Unit)? = null,
        extendControl: ((toHide: Boolean) -> Unit)? = null
    ) {
        refreshListener?.apply { mRefreshPlugin.refreshListener = this }
        this.mExtendControl = extendControl
    }

    var endOfPrepend = false

    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (field != loadState) {
                when (loadState) {
                    is LoadState.Loading -> mRefreshGate.prep(true)
                    is LoadState.Error -> mRefreshGate.prep(false)
                    is LoadState.NotLoading -> {
                        mRefreshGate.trigger()
                        if (mRefreshGate.ignore()) {
                            mRefreshPlugin.isEnable = true
                        }
                    }
                }

                field = loadState
            }
        }


}