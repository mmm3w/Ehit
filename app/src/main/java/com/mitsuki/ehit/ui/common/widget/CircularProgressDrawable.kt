package com.mitsuki.ehit.ui.common.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.floor

class CircularProgressDrawable : Drawable(), Animatable {
    private val mRing = Ring().apply {
        showArrow = true
    }

    private var mRotation = 0f

    private var mOriginalStart: Float = 0f
    private var mOriginalEnd: Float = 0f
    private var mLastStart: Float = 0f
    private var mLastEnd: Float = 0f

    private val mCoreAnimator: Animator
    private val mContinueAnimator: Animator
    private var mRotationCount = 0f
    private var mFinishing = false
    private var mContinue = false


    companion object {
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
        private val MATERIAL_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()

        private const val SHRINK_OFFSET = 0.5f
        private const val ANIMATION_DURATION = 1332
        private const val GROUP_FULL_ROTATION = 1080f / 5f
        private const val MAX_PROGRESS_ARC = .8f
        private const val MIN_PROGRESS_ARC = .01f
        private const val RING_ROTATION = 1f - (MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
    }


    init {
        val ring: Ring = mRing

        mContinueAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
            animator.interpolator = LINEAR_INTERPOLATOR
            animator.addUpdateListener { animation ->
                val interpolatedTime = animation.animatedValue as Float
                applyContinueTranslation(interpolatedTime, ring)
                invalidateSelf()
            }
            animator.addListener(onEnd = {
                ring.showArrow = true
                ring.resetOriginals()

                ring.startTrim = mOriginalStart
                ring.endTrim = mOriginalEnd
                invalidateSelf()
            })
        }

        mCoreAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
            animator.addUpdateListener { animation ->
                val interpolatedTime = animation.animatedValue as Float
                applyTransformation(interpolatedTime, ring, false)
                invalidateSelf()
            }
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.RESTART
            animator.interpolator = LINEAR_INTERPOLATOR
            animator.addListener(
                onStart = {
                    mRotationCount = 0f
                    ring.showArrow = false
                },
                onCancel = {
                    if (mContinue) {
                        mContinueAnimator.cancel()
                        ring.fixOriginals(mRotation / 360f)
                        mRotation = 0f
                        mLastStart = ring.startTrim
                        mLastEnd = ring.endTrim

                        val mStartMileage = rotationStep(mLastStart % 1, mOriginalStart)
                        val mEndMileage =
                            mStartMileage + mLastStart - mOriginalStart + mOriginalEnd - mLastEnd
                        mContinueAnimator.duration =
                            (ANIMATION_DURATION / 2 * mStartMileage.coerceAtLeast(mEndMileage)).toLong()

                        mContinueAnimator.start()
                    }
                },
                onRepeat = {
                    applyTransformation(1f, ring, true)
                    ring.storeOriginals()
                    when {
                        mFinishing -> {
                            mFinishing = false
                            animator.cancel()
                            animator.duration = ANIMATION_DURATION.toLong()
                            animator.start()
                        }
                        else -> mRotationCount += 1
                    }
                }
            )
        }

    }

    var strokeWidth: Float
        get() = mRing.strokeWidth
        set(value) {
            if (mRing.strokeWidth != value) {
                mRing.strokeWidth = value
                invalidateSelf()
            }
        }

    var centerRadius: Float
        get() = mRing.ringCenterRadius
        set(value) {
            if (mRing.ringCenterRadius != value) {
                mRing.ringCenterRadius = value
                invalidateSelf()
            }
        }

    var color: Int
        get() = mRing.currentColor
        set(value) {
            if (mRing.currentColor != value) {
                mRing.currentColor = value
                invalidateSelf()
            }
        }

    fun setArrowDimensions(width: Float, height: Float) {
        mRing.setArrowDimensions(width, height)
    }

    fun setStartEndTrim(start: Float, end: Float) {
        mOriginalStart = start
        mOriginalEnd = end
        mRing.startTrim = start
        mRing.endTrim = end
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        canvas.save()
        canvas.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY())
        mRing.draw(canvas, bounds)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        mRing.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        colorFilter?.apply {
            mRing.colorFilter = this
            invalidateSelf()
        }
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun start() {
        mContinue = false
        mCoreAnimator.cancel()
        mContinueAnimator.cancel()
        mRing.storeOriginals()
        if (mRing.endTrim != mRing.startTrim) {
            mFinishing = true
            mCoreAnimator.duration = (ANIMATION_DURATION / 2).toLong()
            mCoreAnimator.start()
        } else {
            mRing.resetOriginals()
            mCoreAnimator.duration = ANIMATION_DURATION.toLong()
            mCoreAnimator.start()
        }
    }

    override fun stop() {
        mContinue = true
        mCoreAnimator.cancel()
    }

    override fun isRunning(): Boolean {
        return mCoreAnimator.isRunning || mContinueAnimator.isRunning
    }

    private fun applyTransformation(interpolatedTime: Float, ring: Ring, lastFrame: Boolean) {
        when {
            mFinishing -> applyFinishTranslation(interpolatedTime, ring)
            interpolatedTime != 1f || lastFrame -> {
                val startingRotation: Float = ring.startingRotation
                val startTrim: Float
                val endTrim: Float

                if (interpolatedTime < SHRINK_OFFSET) { // Expansion occurs on first half of animation
                    val scaledTime = interpolatedTime / SHRINK_OFFSET
                    startTrim = ring.startingStartTrim
                    endTrim =
                        startTrim + ((MAX_PROGRESS_ARC - MIN_PROGRESS_ARC) * MATERIAL_INTERPOLATOR.getInterpolation(
                            scaledTime
                        ) + MIN_PROGRESS_ARC)
                } else { // Shrinking occurs on second half of animation
                    val scaledTime = (interpolatedTime - SHRINK_OFFSET) / (1f - SHRINK_OFFSET)
                    endTrim = ring.startingStartTrim + (MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
                    startTrim =
                        endTrim - ((MAX_PROGRESS_ARC - MIN_PROGRESS_ARC) * (1f - MATERIAL_INTERPOLATOR.getInterpolation(
                            scaledTime
                        )) + MIN_PROGRESS_ARC)
                }

                val rotation =
                    startingRotation + RING_ROTATION * interpolatedTime
                val groupRotation =
                    GROUP_FULL_ROTATION * (interpolatedTime + mRotationCount)

                ring.startTrim = startTrim
                ring.endTrim = endTrim
                ring.rotation = rotation
                mRotation = groupRotation
            }
        }
    }

    private fun applyFinishTranslation(interpolatedTime: Float, ring: Ring) {
        val targetRotation =
            (floor((ring.startingRotation / MAX_PROGRESS_ARC).toDouble()) + 1f).toFloat()
        val startTrim: Float = (ring.startingStartTrim
                + (ring.startingEndTrim - MIN_PROGRESS_ARC - ring.startingStartTrim)
                * interpolatedTime)
        ring.startTrim = startTrim
        ring.endTrim = ring.startingEndTrim
        val rotation: Float = (ring.startingRotation
                + (targetRotation - ring.startingRotation) * interpolatedTime)
        ring.rotation = rotation
    }

    private fun applyContinueTranslation(interpolatedTime: Float, ring: Ring) {
        val mStartMileage = rotationStep(mLastStart % 1, mOriginalStart)
        val mEndMileage = mStartMileage + mLastStart - mOriginalStart + mOriginalEnd - mLastEnd

        ring.startTrim = mStartMileage * interpolatedTime + mLastStart
        ring.endTrim = mEndMileage * interpolatedTime + mLastEnd

        Log.d(
            "asdf",
            "${ring.startTrim + ring.rotation + mRotation / 360f} | ${ring.endTrim + ring.rotation + mRotation / 360f}"
        )
    }

    private fun rotationStep(current: Float, target: Float): Float {
        return if (target > current) {
            target - current
        } else {
            1 - current + target
        }
    }

    class Ring {
        private val mTempBounds = RectF()
        private val mPaint = Paint()
        private val mArrowPaint = Paint()

        var strokeWidth = 5f
            set(value) {
                if (value != field) {
                    mPaint.strokeWidth = value
                    field = value
                }
            }
        var ringCenterRadius = 0f
        private var mArrowWidth = 0
        private var mArrowScale = 1f

        var startTrim = 0f
        var endTrim = 0f
        var rotation = 0f

        var currentColor = 0
        var alpha = 255

        private lateinit var mArrow: Path
        var showArrow = false
        private var mArrowHeight = 0

        /*----------------------------------------------------------------------------------------*/
        var startingRotation: Float = 0f
        var startingStartTrim: Float = 0f
        var startingEndTrim: Float = 0f


        var colorFilter: ColorFilter
            get() = mPaint.colorFilter
            set(value) {
                mPaint.colorFilter = value
            }

        init {
            mPaint.strokeCap = Paint.Cap.SQUARE
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.STROKE

            mArrowPaint.style = Paint.Style.FILL
            mArrowPaint.isAntiAlias = true
        }

        fun setArrowDimensions(width: Float, height: Float) {
            mArrowWidth = width.toInt()
            mArrowHeight = height.toInt()
        }

        fun storeOriginals() {
            startingStartTrim = startTrim
            startingEndTrim = endTrim
            startingRotation = rotation
        }

        fun fixOriginals(r: Float) {
            startTrim += r + rotation
            endTrim += r + rotation
            rotation = 0f
        }

        fun resetOriginals() {
            startingStartTrim = 0f
            startingEndTrim = 0f
            startingRotation = 0f
            startTrim = 0f
            endTrim = 0f
            rotation = 0f
        }

        fun draw(c: Canvas, bounds: Rect) {
            val arcBounds = mTempBounds
            var arcRadius: Float = ringCenterRadius + strokeWidth / 2f
            if (ringCenterRadius <= 0) {
                arcRadius = bounds.width()
                    .coerceAtMost(bounds.height()) / 2f - (mArrowWidth * mArrowScale / 2f).coerceAtLeast(
                    strokeWidth / 2f
                )
            }
            arcBounds[bounds.centerX() - arcRadius, bounds.centerY() - arcRadius, bounds.centerX() + arcRadius] =
                bounds.centerY() + arcRadius

            val startAngle: Float = (startTrim + rotation) * 360
            val endAngle: Float = (endTrim + rotation) * 360
            val sweepAngle = endAngle - startAngle

            mPaint.color = currentColor
            mPaint.alpha = alpha

            val inset: Float = strokeWidth / 2f // Calculate inset to draw inside the arc
            arcBounds.inset(-inset, -inset) // Revert the inset

            c.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint)

            drawTriangle(c, startAngle, sweepAngle, arcBounds)
        }

        private fun drawTriangle(
            c: Canvas,
            startAngle: Float,
            sweepAngle: Float,
            bounds: RectF
        ) {
            if (showArrow) {
                if (!this::mArrow.isInitialized) {
                    mArrow = Path().apply { fillType = Path.FillType.EVEN_ODD }
                } else {
                    mArrow.reset()
                }
                val centerRadius: Float =
                    bounds.width().coerceAtMost(bounds.height()) / 2f
                val inset: Float = mArrowWidth * mArrowScale / 2f
                mArrow.moveTo(0f, 0f)
                mArrow.lineTo(mArrowWidth * mArrowScale, 0f)
                mArrow.lineTo(
                    (mArrowWidth * mArrowScale / 2), ((mArrowHeight
                            * mArrowScale))
                )
                mArrow.offset(
                    centerRadius + bounds.centerX() - inset,
                    bounds.centerY() + strokeWidth / 2f
                )
                mArrow.close()
                // draw a triangle
                mArrowPaint.color = currentColor
                mArrowPaint.alpha = alpha
                c.save()
                c.rotate(
                    startAngle + sweepAngle, bounds.centerX(),
                    bounds.centerY()
                )
                c.drawPath(mArrow, mArrowPaint)
                c.restore()
            }
        }

    }
}