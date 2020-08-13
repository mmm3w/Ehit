package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.marginVertical
import com.mitsuki.ehit.R
import kotlin.math.roundToInt

@Suppress("JoinDeclarationAndAssignment", "ConstantConditionIf")
class GalleryListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent3 {

    private var mRecyclerView: RecyclerView //数据列表
    private val mTopBar: View //顶部的view

    private val mNextPageScrollSize: Int

    private val mRefreshView: CircleRefreshView //刷新View
    private val mProgress: CircularProgressDrawable //刷新View中的进度条
    private val mLoadView: ImageView //加载View
    private val mLoadTricolorBar: TricolorBarDrawable //加载View中的进度条

    //基础配置参数
    private val mCircleDiameter: Int //刷新View的大小
    private val mStatusBarHeight: Int //状态栏的高度

    //View状态参数
    private var mTopBarOffset = 0f //顶部View的偏移量  注意状态保存
    private var mOffsetLimit: Float = 0f //顶部View最大的偏移量

    private val mIgnoredStatusBar = false //是否ignore state bar
    private val mTopOffset = if (mIgnoredStatusBar) statusBarHeight() else 0 //state bar偏移

    private var mRefreshing = false //刷新状态
    private var mEnableRefresh = true //刷新是否可用
    private var mLoading = false
    private var mEnableLoadTrigger = true

    private var mRefreshThreshold: Float //触发刷新的阈值，以及下拉阻力增强的阈值
    private var mCurrentRefreshDragY = 0f //刷新用下拉距离
    private var mFrom = 0f //刷新View回弹起始点
    private var mCurrentLoadDragY = 0f //加载用上拉距离
    private var mLoadRange: Float //加载上拉换算


    private var mAlphaStartAnimation: Animation? = null //progress 透明度变化动画
    private var mAlphaMaxAnimation: Animation? = null //progress 透明度变化动画
    private val mDecelerateInterpolator =
        DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR) //插值器


    companion object {
        private const val SHADOW_ELEVATION = 4
        private const val CIRCLE_DIAMETER = 40
        private const val DEFAULT_CIRCLE_TARGET = 128
        private const val MAX_PROGRESS_ANGLE = .8f
        private const val ALPHA_ANIMATION_DURATION = 300
        private const val DECELERATE_INTERPOLATION_FACTOR = 2f

        private const val MAX_ALPHA = 255
        private const val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()
        private const val SCALE_DURATION = 150
        private const val BACK_DURATION = 100

        private const val LOAD_RANGE = 200
    }

    init {

        resources.displayMetrics.density.apply {
            mCircleDiameter = (CIRCLE_DIAMETER * this).roundToInt()
            mRefreshThreshold = DEFAULT_CIRCLE_TARGET * this
            mLoadRange = dp2px(LOAD_RANGE.toFloat()).toFloat()
            mNextPageScrollSize = dp2px(48f)
        }
        mStatusBarHeight = statusBarHeight()

        //创建 RecyclerView
        mRecyclerView = RecyclerView(context).apply {
            id = R.id.gallery_list_inner_recyclerview
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        addView(mRecyclerView)
        //创建刷新View
        mRefreshView =
            CircleRefreshView(context).apply { id = R.id.gallery_list_inner_refresh_view }
        mProgress = CircularProgressDrawable(context)
        mProgress.setStyle(CircularProgressDrawable.DEFAULT)
        mRefreshView.setImageDrawable(mProgress)
        mRefreshView.visibility = View.GONE
        addView(mRefreshView)
        //创建top bar
        mTopBar = LayoutInflater.from(context).inflate(R.layout.viewgroup_top_bar, this, false)
        addView(mTopBar)
        //创建load bar
        mLoadView = ImageView(context).apply { id = R.id.gallery_list_inner_load_view }
        mLoadTricolorBar = TricolorBarDrawable()
        mLoadView.setImageDrawable(mLoadTricolorBar)
        addView(mLoadView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mTopBar.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )

        mRecyclerView.setPadding(
            0,
            mTopBar.measuredHeight + mTopBar.marginVertical() + mStatusBarHeight - mTopOffset,
            0,
            0
        )
        mRecyclerView.measure(
            MeasureSpec.makeMeasureSpec(this@GalleryListLayout.measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                this@GalleryListLayout.measuredHeight - mTopOffset,
                MeasureSpec.EXACTLY
            )
        )

        mRefreshView.measure(
            MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY)
        )

        mLoadView.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(dp2px(4f), MeasureSpec.EXACTLY)
        )

        //偏移量计算
        mOffsetLimit =
            (mTopBar.measuredHeight + mTopBar.marginVertical() + mStatusBarHeight).toFloat()

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mTopBar.apply {
            layout(0, mStatusBarHeight, measuredWidth, mStatusBarHeight + measuredHeight)
        }

        mRefreshView.apply {
            layout(
                this@GalleryListLayout.measuredWidth / 2 - measuredWidth / 2,
                mTopOffset + mTopBar.measuredHeight,
                this@GalleryListLayout.measuredWidth / 2 + measuredWidth / 2,
                measuredHeight + mTopOffset + mTopBar.measuredHeight
            )
        }

        mRecyclerView.apply { layout(0, mTopOffset, measuredWidth, measuredHeight + mTopOffset) }

        mLoadView.apply {
            layout(
                0, this@GalleryListLayout.measuredHeight - measuredHeight,
                measuredWidth, this@GalleryListLayout.measuredHeight
            )
        }
    }

    /** nested scroll *****************************************************************************/
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            mCurrentRefreshDragY = 0f
            mCurrentLoadDragY = 0f
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {

        if (dy > 0) {
            //向上滑动

            //三个条件：1、向上移动的距离没有达到阈值。2、滚动View能够向上滚动(能够一起联动)。3、除去刷新View已被拉下，并可以使用状态外
            if (mTopBarOffset > -mOffsetLimit && target.canScrollVertically(1) && (mRefreshView.translationY <= 0 || !refreshScroll())) {
                if (mTopBarOffset - dy < -mOffsetLimit) mTopBarOffset = -mOffsetLimit
                else mTopBarOffset -= dy
                mTopBar.translationY = mTopBarOffset
            }

            if (type != ViewCompat.TYPE_TOUCH) return //fling隔离

            //两个条件：1、刷新View已经被拉下来了。2、刷新是可用状态
            if (mRefreshView.translationY > 0 && refreshScroll()) {
                mCurrentRefreshDragY -= dy
                consumed[1] = dy
                moveSpinner()
            }

            //两个条件：1、不可上拉。2、加载可触发
            if (loadTrigger()) {
                if (!mRecyclerView.canScrollVertically(1)) {
                    mCurrentLoadDragY += dy
                    consumed[1] = dy
                    extendLoad()
                } else {
                    //上拉的过程中，忽然加载出数据导致上拉可用
                    //清空上拉距离，并将加载归位
                    if (mCurrentLoadDragY > 0) {
                        mCurrentLoadDragY = 0f
                        mLoadTricolorBar.back()
                    }
                }
            }


        }

        if (dy < 0) {
            //向下滑动

            //三个条件：1、向下移动没有达到阈值。2、滚动View能够向下滚动(能够一起联动)。3、loadView还有进度
            if (mTopBarOffset < 0 && mRecyclerView.canScrollVertically(-1) && mCurrentLoadDragY <= 0) {
                if (mTopBarOffset > dy) mTopBarOffset = 0f
                else mTopBarOffset -= dy
                mTopBar.translationY = mTopBarOffset
            }

            if (type != ViewCompat.TYPE_TOUCH) return //fling隔离

            //两个条件：1、滚动View已经不能被下拉。2、刷新是可用状态
            if (!mRecyclerView.canScrollVertically(-1) && refreshScroll()) {
                mCurrentRefreshDragY -= dy
                consumed[1] = dy
                moveSpinner()
            }

            //两个条件：1、上拉是可用。2、load以延展
            if (loadTrigger()) {
                //同样要考虑忽然出现数据加载的情况
                //直接恢复并不再消费距离
                if (!mRecyclerView.canScrollVertically(1)) {
                    if (mCurrentLoadDragY > 0) {
                        mCurrentLoadDragY += dy
                        consumed[1] = dy
                        extendLoad()
                    }
                } else {
                    if (mCurrentLoadDragY > 0) {
                        mCurrentLoadDragY = 0f
                        mLoadTricolorBar.back()
                    }
                }
            }
        }
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
            if (mCurrentRefreshDragY > 0) {
                finishSpinner()
                mCurrentRefreshDragY = 0f
            }

            if (mCurrentLoadDragY > 0) {
                triggerLoad()
                mCurrentLoadDragY = 0f
            }
        }
    }

    /** for in ************************************************************************************/
    private fun moveSpinner() {
        //move需要处理的内容
        //1、换算下拉比例，计算刷新View向下移动的距离
        //2、换算下拉比例，计算刷新View中的progress的进度

        //view状态处理
        if (mRefreshView.visibility != View.VISIBLE) mRefreshView.visibility = View.VISIBLE

        //比例换算
        val dragPercent = (mCurrentRefreshDragY / mRefreshThreshold).coerceAtLeast(0f)
        val adjustedPercent = damp(dragPercent)

        //progress透明度
        if (mCurrentRefreshDragY < mRefreshThreshold) {
            if (mProgress.alpha > STARTING_PROGRESS_ALPHA && !mAlphaStartAnimation.isAnimationRunning()) {
                startProgressAlphaStartAnimation()
            }
        } else {
            if (mProgress.alpha < MAX_ALPHA && !mAlphaMaxAnimation.isAnimationRunning()) {
                startProgressAlphaMaxAnimation()
            }
        }

        //view效果处理
        setAnimationScale(dragPercent.coerceAtMost(1f))
        val strokeStart = adjustedPercent.coerceAtMost(1f) * .8f
        mProgress.setStartEndTrim(0f, MAX_PROGRESS_ANGLE.coerceAtMost(strokeStart))
        mProgress.arrowScale = 1f.coerceAtMost(adjustedPercent)
        mProgress.progressRotation = adjustedPercent
        mRefreshView.translationY = adjustedPercent * mRefreshThreshold
    }

    private fun finishSpinner() {
        mFrom = mCurrentRefreshDragY
        mRefreshing = mCurrentRefreshDragY > mRefreshThreshold
        startBackAnimation()
    }

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

    private fun resetRefresh() {
        mRefreshView.clearAnimation()
        mRefreshView.visibility = View.GONE
        mProgress.stop()
        mProgress.alpha = MAX_ALPHA
        mRefreshView.background.alpha = MAX_ALPHA

        setAnimationScale(0f)
    }

    private fun refreshScroll(): Boolean {
        return !mRefreshing && mEnableRefresh
    }

    private fun loadTrigger(): Boolean {
        return !mLoading && mEnableLoadTrigger
    }

    private fun setAnimationScale(progress: Float) {
        mRefreshView.scaleX = progress
        mRefreshView.scaleY = progress
    }

    private fun statusBarHeight(): Int {
        return resources.getDimensionPixelSize(
            context.resources.getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
            )
        )
    }

    @Suppress("unused")
    private fun navigationBarHeight(): Int {
        return resources.getDimensionPixelSize(
            context.resources.getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
            )
        )
    }

    private fun damp(source: Float): Float {
        return (-2f / (source + 1f) + 2f) * 0.8f
    }

    /** animation *********************************************************************************/

    //回弹回调
    private val mAnimationEnd: () -> Unit = {
        if (mRefreshing) {
            mProgress.alpha = MAX_ALPHA
            mProgress.start()

            //回调刷新，如果没有回调，则3s后自动关闭刷新
            (refreshListener ?: {
                postDelayed({ isRefreshing = false }, 3000)
            }).invoke()
        } else {
            resetRefresh()
        }
    }

    //回弹动画
    private val mAnimateToBack = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val tempDistance = if (!mRefreshing) (1 - interpolatedTime) * mFrom
            else (1 - interpolatedTime) * (mFrom - mRefreshThreshold) + mRefreshThreshold
            val dragPercent = (tempDistance / mRefreshThreshold).coerceAtLeast(0f)
            val adjustedPercent = damp(dragPercent)
            setAnimationScale(dragPercent.coerceAtMost(1f))
            mRefreshView.translationY = adjustedPercent * mRefreshThreshold
        }
    }

    private fun Animation?.isAnimationRunning(): Boolean {
        if (this == null) return false
        return hasStarted() && !hasEnded()
    }


    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation =
            startAlphaAnimation(mProgress.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.alpha, MAX_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation {
        return object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress.alpha =
                    (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }.apply {
            duration = ALPHA_ANIMATION_DURATION.toLong()
            mRefreshView.setAnimationListener(null)
            mRefreshView.clearAnimation()
            mRefreshView.startAnimation(this)
        }
    }

    private fun startScaleUpAnimation() {
        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationScale(interpolatedTime)
            }
        }.apply {
            duration = SCALE_DURATION.toLong()
            mRefreshView.setAnimationListener(mAnimationEnd)
            mRefreshView.clearAnimation()
            mRefreshView.startAnimation(this)
        }
    }

    private fun startScaleDownAnimation() {
        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationScale(1 - interpolatedTime)
            }
        }.apply {
            duration = SCALE_DURATION.toLong()
            mRefreshView.setAnimationListener(mAnimationEnd)
            mRefreshView.clearAnimation()
            mRefreshView.startAnimation(this)
        }
    }

    private fun startBackAnimation() {
        mAnimateToBack.apply {
            reset()
            duration = BACK_DURATION.toLong()
            interpolator = mDecelerateInterpolator
            mRefreshView.setAnimationListener(mAnimationEnd)
            mRefreshView.clearAnimation()
            mRefreshView.startAnimation(this)
        }
    }

    /** for out ***********************************************************************************/
    fun recyclerView(action: RecyclerView.() -> Unit) = mRecyclerView.apply(action)

    fun topBar(action: View.() -> Unit) = mTopBar.apply(action)

    var refreshListener: (() -> Unit)? = null

    //用变量的getter和setter替代get和set方法
    var isRefreshing: Boolean
        set(value) {
            if (mRefreshing == value) return

            mRefreshing = value
            if (mRefreshing) {
                //就地开启刷新
                if (mRefreshView.visibility != View.VISIBLE) mRefreshView.visibility = View.VISIBLE
                mProgress.alpha = MAX_ALPHA
                mRefreshView.translationY = mRefreshThreshold
                startScaleUpAnimation()
            } else {
                //关闭刷新
                startScaleDownAnimation()
            }
        }
        get() = mRefreshing

    var loadListener: (() -> Unit)? = null

    fun setLoading(loading: Boolean, error: Boolean) {
        if (mLoading == loading) return
        mLoading = loading
        if (mLoading) {
            mLoadTricolorBar.start()
        } else {
            mLoadTricolorBar.stop()
            //尝试将recyclerview向上滚动一段距离？
            //增加error防止错误上移
            if (!error && mRecyclerView.canScrollVertically(-1)) {
                mRecyclerView.smoothScrollBy(0, mNextPageScrollSize)
            }
        }
    }
}