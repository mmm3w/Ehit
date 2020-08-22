package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mitsuki.armory.extend.*
import com.mitsuki.ehit.R
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("JoinDeclarationAndAssignment", "ConstantConditionIf")
class GalleryListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent3 {

    private var mRecyclerView: RecyclerView //数据列表

    private val mNextPageScrollSize: Int
    private val marginSize: Int

    //基础配置参数
    private val mCircleDiameter: Int //刷新View的大小
    private val mStatusBarHeight: Int //状态栏的高度
    private val mNavigationBarHeight: Int //底栏高度
    private val mFabSlop: Int

    private val mIgnoredStatusBar = false //是否ignore state bar
    private val mTopOffset: Int //state bar偏移

    private val mRefreshPlugin: RefreshPlugin
    private val mTopBarPlugin: TopBarPlugin

    private var mExtendControl: ((toHide: Boolean) -> Unit)? = null
    private var mExtendTag = false

    companion object {
        private const val CIRCLE_DIAMETER = 40
    }

    init {
        resources.displayMetrics.density.apply {
            mCircleDiameter = (CIRCLE_DIAMETER * this).roundToInt()
            mNextPageScrollSize = dp2px(48f)
            marginSize = dp2px(36f)
        }
        mStatusBarHeight = statusBarHeight(context)
        mNavigationBarHeight = navigationBarHeight(context)
        mTopOffset = if (mIgnoredStatusBar) mStatusBarHeight else 0
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
        mTopBarPlugin =
            TopBarPlugin(context, R.layout.viewgroup_top_bar, this).apply { addView(view()) }

        mRefreshPlugin.additional = {
            if (!endOfPrepend)
                false
            else
                if (it > 0)
                    true
                else
                    !mRecyclerView.canScrollVertically(-1)
        }

        mRecyclerView.addOnScrollListener(mTopBarPlugin)
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
        mTopBarPlugin.view().measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )

        mRecyclerView.setPadding(
            0,
            mTopBarPlugin.view().measuredHeight + mTopBarPlugin.view().marginVertical() +
                    mStatusBarHeight - mTopOffset,
            0,
            mNavigationBarHeight
        )

        mRecyclerView.measure(
            MeasureSpec.makeMeasureSpec(this@GalleryListLayout.measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                this@GalleryListLayout.measuredHeight - mTopOffset,
                MeasureSpec.EXACTLY
            )
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
        mTopBarPlugin.view {
            layout(0, mStatusBarHeight, measuredWidth, mStatusBarHeight + measuredHeight)
        }

        mRefreshPlugin.view {
            layout(
                this@GalleryListLayout.measuredWidth / 2 - measuredWidth / 2,
                mTopOffset + mTopBarPlugin.view().measuredHeight,
                this@GalleryListLayout.measuredWidth / 2 + measuredWidth / 2,
                measuredHeight + mTopOffset + mTopBarPlugin.view().measuredHeight
            )
        }

        mRecyclerView.apply { layout(0, mTopOffset, measuredWidth, measuredHeight + mTopOffset) }

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

    fun topBar(action: View.() -> Unit) = mTopBarPlugin.view(action)

    fun setListener(
        refreshListener: (() -> Unit)? = null,
        extendControl: ((toHide: Boolean) -> Unit)? = null
    ) {
        refreshListener?.apply { mRefreshPlugin.refreshListener = this }
        this.mExtendControl = extendControl
    }

    //用变量的getter和setter替代get和set方法
    var isRefreshing: Boolean
        set(value) {
            mRefreshPlugin.isRefreshing = value
        }
        get() = mRefreshPlugin.isRefreshing

    var endOfPrepend = false

}