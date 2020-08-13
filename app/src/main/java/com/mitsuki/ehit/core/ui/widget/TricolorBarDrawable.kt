package com.mitsuki.ehit.core.ui.widget

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener

class TricolorBarDrawable : Drawable(), Animatable {

    private val mLinearInterpolator: Interpolator = LinearInterpolator()
    private var mAnimator: ValueAnimator? = null
    private var mStartAnimator: ValueAnimator? = null
    private var mEndAnimator: ValueAnimator? = null

    private val mDuration = 600

    private val mBar = Bar()

    private val mListener: ValueAnimator.AnimatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener {
            mBar.progress = it.animatedValue as Float
            invalidateSelf()
        }

    private var mFinishTag = false

    init {
        mBar.reset()
        setupAnimators()
    }

    fun setProgress(progress: Float) {
        if (mBar.mode == Bar.LoadMode.Loading) return
        mBar.progress = progress.coerceIn(0f, 1f)
        invalidateSelf()
    }

    fun back() {
        if (mBar.mode != Bar.LoadMode.Start || mBar.progress == 0f) return
        mStartAnimator?.cancel()
        mAnimator?.cancel()
        mEndAnimator?.cancel()

        mEndAnimator?.apply {
            setFloatValues(mBar.progress, 0f)
            duration = (mBar.progress * mDuration).toLong()
            start()
        }
    }

    /** Drawable **********************************************************************************/
    override fun draw(canvas: Canvas) {
        val bounds = bounds
        canvas.save()
        mBar.draw(canvas, bounds)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        invalidateSelf()
    }

    /** Animatable ********************************************************************************/
    override fun isRunning(): Boolean = mAnimator?.isRunning ?: false

    override fun start() {
        if (mBar.mode != Bar.LoadMode.Start) return

        //关闭所有动画
        mFinishTag = false
        mStartAnimator?.cancel()
        mAnimator?.cancel()
        mEndAnimator?.cancel()

        mStartAnimator?.apply {
            setFloatValues(mBar.progress, 1f)
            duration = ((1 - mBar.progress) * mDuration).toLong()
            start()
        }
    }

    override fun stop() {
        mFinishTag = true
    }

    /**********************************************************************************************/
    private fun setupAnimators() {
        mStartAnimator = ValueAnimator.ofFloat(mBar.progress, 1f).apply {
            interpolator = mLinearInterpolator
            addUpdateListener(mListener)
            addListener(
                onEnd = {
                    if (mFinishTag) {
                        mBar.mode = Bar.LoadMode.End
                        mEndAnimator?.apply {
                            setFloatValues(0f, 1f)
                            duration = mDuration.toLong()
                            start()
                        }
                    } else {
                        mBar.mode = Bar.LoadMode.Loading
                        mBar.goToNextColor()
                        mAnimator?.apply {
                            duration = mDuration.toLong()
                            start()
                        }
                    }
                }
            )
        }

        mAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = mLinearInterpolator
            addUpdateListener(mListener)
            addListener(
                onRepeat = {
                    mBar.goToNextColor()
                    if (mFinishTag) {
                        mAnimator?.cancel()
                        mBar.mode = Bar.LoadMode.End
                        mEndAnimator?.apply {
                            setFloatValues(0f, 1f)
                            duration = mDuration.toLong()
                            start()
                        }
                    }
                }
            )
        }

        mEndAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = mLinearInterpolator
            addUpdateListener(mListener)
            addListener(
                onEnd = {
                    mBar.reset()
                }
            )
        }
    }

    private class Bar {
        private val mPaint = Paint()
        private val mTempLeftBounds = RectF()
        private val mTempRightBounds = RectF()

        var mode: LoadMode = LoadMode.Start
        var progress: Float = 0f

        private var mColorIndex = 0
        private var mLastColorIndex: Int = -1
        private var mColors: IntArray =
            intArrayOf(
                0xffff6464.toInt(),
                0xff6464ff.toInt(),
                0xff64ff64.toInt(),
                0xffffff64.toInt()
            )

        init {
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.FILL
        }

        fun draw(c: Canvas, bounds: Rect) {
            val width = (bounds.right - bounds.left) / 2

            val left = mTempLeftBounds
            val right = mTempRightBounds

            if (mode == LoadMode.Start) {
                left.set(
                    bounds.centerX() - width * progress,
                    bounds.top.toFloat(),
                    bounds.centerX().toFloat(),
                    bounds.bottom.toFloat()
                )
                right.set(
                    bounds.centerX().toFloat(),
                    bounds.top.toFloat(),
                    bounds.centerX() + width * progress,
                    bounds.bottom.toFloat()
                )
                mPaint.color = mColors[mColorIndex]
            } else {
                if (mode == LoadMode.Loading) {
                    if (mColorIndex >= 0) {
                        mPaint.color = mColors[mColorIndex]
                        c.drawRect(bounds, mPaint)
                    }
                }

                left.set(
                    bounds.left.toFloat(),
                    bounds.top.toFloat(),
                    bounds.centerX() - width * progress,
                    bounds.bottom.toFloat()
                )
                right.set(
                    bounds.centerX().toFloat() + width * progress,
                    bounds.top.toFloat(),
                    bounds.right.toFloat(),
                    bounds.bottom.toFloat()
                )
                mPaint.color = mColors[mLastColorIndex]
            }

            c.drawRect(left, mPaint)
            c.drawRect(right, mPaint)
        }

        private fun getNextColorIndex(): Int {
            return (mColorIndex + 1) % mColors.size
        }

        fun goToNextColor() {
            mLastColorIndex = mColorIndex
            mColorIndex = getNextColorIndex()
        }

        fun reset() {
            mLastColorIndex = 0
            mColorIndex = 0
            progress = 0f
            mode = LoadMode.Start
        }

        enum class LoadMode {
            Start, Loading, End
        }
    }

}