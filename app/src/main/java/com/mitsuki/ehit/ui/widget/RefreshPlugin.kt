package com.mitsuki.ehit.ui.widget

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.mitsuki.armory.extend.isAnimationRunning
import com.mitsuki.ehit.R

class RefreshPlugin(context: Context) {

    private var mRefreshing = false //刷新状态
    private var mEnableRefresh = false //刷新是否可用

    private var mRefreshThreshold: Float //触发刷新的阈值，以及下拉阻力增强的阈值
    private var mCurrentRefreshDragY = 0f //刷新用下拉距离
    private var mFrom = 0f //刷新View回弹起始点

    private var mAlphaStartAnimation: Animation? = null //progress 透明度变化动画
    private var mAlphaMaxAnimation: Animation? = null //progress 透明度变化动画

    private val mRefreshView: CircleRefreshView =
        CircleRefreshView(context).apply { id = R.id.gallery_list_inner_refresh_view }
    private val mProgress: CircularProgressDrawable =
        CircularProgressDrawable(context)


    companion object {
        private const val DEFAULT_CIRCLE_TARGET = 64
        private const val MAX_ALPHA = 255
        private const val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()
        private const val MAX_PROGRESS_ANGLE = .8f
        private const val ALPHA_ANIMATION_DURATION = 300
        private const val BACK_DURATION = 100
        private const val DECELERATE_INTERPOLATION_FACTOR = 2f
        private const val SCALE_DURATION = 150
    }

    init {
        context.resources.displayMetrics.density.apply {
            mRefreshThreshold = DEFAULT_CIRCLE_TARGET * this
        }

        mProgress.setStyle(CircularProgressDrawable.DEFAULT)
        mRefreshView.setImageDrawable(mProgress)
        mRefreshView.visibility = View.GONE
    }

    private val mDecelerateInterpolator =
        DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR) //插值器

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

    //回弹回调
    private val mAnimationEnd: () -> Unit = {
        if (mRefreshing) {
            mProgress.alpha = MAX_ALPHA
            mProgress.start()
            refreshListener?.invoke()
        } else {
            resetRefresh()
        }
    }

    /**********************************************************************************************/
    fun view(action: (CircleRefreshView.() -> Unit)? = null) =
        action?.run { mRefreshView.apply(action) } ?: mRefreshView

    //操作中
    val inOperation: Boolean
        get() = mRefreshView.translationY > 0 || mRefreshing

    @Suppress("SpellCheckingInspection")
    val refreshTriggerable: Boolean
        get() = !mRefreshing && mEnableRefresh //能否触发刷新

    var refreshListener: (() -> Unit)? = null
    var additional: ((dy: Int) -> Boolean)? = null

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


    fun startDrag() {
        mCurrentRefreshDragY = 0f
    }

    //返回已经消耗的距离
    fun drag(dy: Int): Int {
        if (dy > 0) {
            //两个条件：1、刷新View已经被拉下来了。2、刷新是可用状态
            if (mRefreshView.translationY > 0 && refreshTriggerable) {
                mCurrentRefreshDragY -= dy
                moveSpinner()
                return dy
            }
        }

        if (dy < 0) {
            //三个条件：1、滚动View已经不能被下拉。2、刷新是可用状态。3、loadView还有进度
            if (additional(dy) && refreshTriggerable) {
                mCurrentRefreshDragY -= dy
                moveSpinner()
                return dy
            }
        }
        return 0
    }

    fun finishDrag() {
        if (mCurrentRefreshDragY > 0) {
            finishSpinner()
            mCurrentRefreshDragY = 0f
        }
    }

    var isEnable: Boolean
        set(value) {
            if (value != mEnableRefresh) {
                mEnableRefresh = value
            }
        }
        get() = mEnableRefresh


    /**********************************************************************************************/
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

    private fun startScaleUpAnimation() {
        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationScale(interpolatedTime)
            }
        }.apply {
            duration = SCALE_DURATION.toLong()
            mRefreshView.setAnimationListener {
                mProgress.alpha = MAX_ALPHA
                mProgress.start()
            }
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

    private fun setAnimationScale(progress: Float) {
        mRefreshView.scaleX = progress
        mRefreshView.scaleY = progress
    }

    private fun resetRefresh() {
        mRefreshView.clearAnimation()
        mRefreshView.visibility = View.GONE
        mProgress.stop()
        mProgress.alpha = MAX_ALPHA
        mRefreshView.background.alpha = MAX_ALPHA

        setAnimationScale(0f)
    }

    private fun additional(dy: Int) = additional?.invoke(dy) ?: true

    /** assist ************************************************************************************/
    private fun damp(source: Float): Float {
        return (-2f / (source + 1f) + 2f) * 0.8f
    }
}