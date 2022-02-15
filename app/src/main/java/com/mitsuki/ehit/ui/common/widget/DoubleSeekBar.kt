package com.mitsuki.ehit.ui.common.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.addListener
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class DoubleSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var thumbSize = 30f
        set(value) {
            if (value != field) {
                field = value
                updateRect()
                invalidate()
            }
        }

    var thumbScale = 2.4f
        set(value) {
            if (value != field) {
                field = value
                updateRect()
                invalidate()
            }
        }

    var thumbColor: Long = 0xffff0000
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    var progressColor: Long = 0xffebebeb
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    var selectedColor: Long = 0xFFff0000
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    var progressWidth = 10f
        set(value) {
            if (value != field) {
                field = value
                if (!mProgressRect.isEmpty) {
                    val centerY = mProgressRect.centerY()
                    mProgressRect.top = centerY - value / 2
                    mProgressRect.bottom = centerY + value / 2
                    updateRect()
                }
                invalidate()
            }
        }

    private var max = 100
    private var min = 0
    private var end = 100
    private var start = 0

    private val mPaint = Paint()
    private var mActivePointerId: Int = -1

    private val mProgressRect = RectF()
    private val mSelectedRect = RectF()
    private val mStartTouchRect = RectF()
    private val mEndTouchRect = RectF()
    private var mTouchX: Float = 0f
    private var mTouchCheck: Int = -1

    private var mDownAnimation: ValueAnimator? = null
    private var mUpAnimation: ValueAnimator? = null
    private var mHasAnimation: Boolean = false
    private var mAnimaValue: Float = 0F
    private var mResetValue: Float = 0F
    private var mTempValue: Float = 0F

    private val maxThumbSize get() = thumbScale * thumbSize
    private val innerThumbSize get() = thumbSize + (thumbScale - 1) * thumbSize / 4L * mAnimaValue
    private val outerThumbSize get() = maxThumbSize * mAnimaValue

    var callback: ((Int, Int) -> Unit)? = null

    fun setMaxValue(v: Int) {
        if (v != max && v >= min) {
            max = v
            if (v < end) end = v
            if (v < start) start = v
            updateRect()
            invalidate()
        }
    }

    fun setMinValue(v: Int) {
        if (v != min && v <= max) {
            min = v
            if (v > start) start = v
            if (v > end) end = v
            updateRect()
            invalidate()
        }
    }

    fun setStartValue(v: Int) {
        if (v != start) {
            start = when {
                v < min -> min
                v > end -> end
                else -> v
            }
            updateRect()
            invalidate()
        }
    }

    fun setEndValue(v: Int) {
        if (v != end) {
            end = when {
                v < start -> start
                v > max -> max
                else -> v
            }
            updateRect()
            invalidate()
        }
    }

    private fun isTabStart(x: Float, y: Float): Boolean {
        return x >= mStartTouchRect.left &&
                x <= mStartTouchRect.right &&
                y >= mStartTouchRect.top &&
                y <= mStartTouchRect.bottom
    }

    private fun isTabEnd(x: Float, y: Float): Boolean {
        return x >= mEndTouchRect.left &&
                x <= mEndTouchRect.right &&
                y >= mEndTouchRect.top &&
                y <= mEndTouchRect.bottom
    }

    private fun realPosition(p: Int): Float {
        return (p - min).toFloat() / (max - min).toFloat() * mProgressRect.width() + mProgressRect.left
    }

    private fun realProgress(x: Float): Int {
        return ((x - mProgressRect.left) / mProgressRect.width() * (max - min) + min).roundToInt()
    }

    private fun attachAlpha(color: Long, alpha: Float): Long {
        return (color.shr(24) * alpha).roundToLong().shl(24) + color.and(0xffffff)
    }

    private fun requestInvalidate() {
        if (!mHasAnimation) invalidate()
    }

    private fun onStartDrag() {
        mUpAnimation?.cancel()
        mResetValue = 0F
        mTempValue = 0F

        if (mDownAnimation.isRunning()) return
        mDownAnimation = ValueAnimator.ofFloat(mAnimaValue, 1f).apply {
            duration = duration()
            addUpdateListener {
                mAnimaValue = it?.animatedValue as Float
                postInvalidateOnAnimation()
            }
            addListener(
                onStart = { mHasAnimation = true },
                onCancel = { mHasAnimation = false },
                onEnd = {
                    mHasAnimation = false
                    mAnimaValue = 1f
                }
            )
            start()
        }

    }

    private fun onFinishDrag() {
        mDownAnimation?.cancel()
        when (mTouchCheck) {
            0 -> {
                val rp = realPosition(start)
                mTempValue = mSelectedRect.left
                mResetValue = rp - mSelectedRect.left
            }
            1 -> {
                val rp = realPosition(end)
                mTempValue = mSelectedRect.right
                mResetValue = rp - mSelectedRect.right
            }
        }

        if (mUpAnimation.isRunning()) return
        mUpAnimation = ValueAnimator.ofFloat(mAnimaValue, 0f).apply {
            duration = duration()
            addUpdateListener {
                mAnimaValue = it?.animatedValue as Float
                when (mTouchCheck) {
                    0 -> setStart((1 - mAnimaValue) * mResetValue + mTempValue)
                    1 -> setEnd((1 - mAnimaValue) * mResetValue + mTempValue)
                }
                postInvalidateOnAnimation()
            }

            addListener(
                onStart = { mHasAnimation = true },
                onCancel = {
                    mHasAnimation = false
                    mTouchCheck = -1
                    mResetValue = 0F
                    mTempValue = 0F
                },
                onEnd = {
                    mHasAnimation = false
                    mAnimaValue = 0f
                    mResetValue = 0F
                    mTempValue = 0F
                    mTouchCheck = -1
                }
            )
            start()
        }
    }

    private fun updateRect() {
        val progressLength = mProgressRect.width()
        val centerY = mProgressRect.centerY()

        val lp = (start - min) / (max - min).toFloat()
        val rp = (end - min) / (max - min).toFloat()
        val spl = mProgressRect.left + lp * progressLength
        val spr = mProgressRect.left + rp * progressLength
        mSelectedRect.set(spl, mProgressRect.top, spr, mProgressRect.bottom)

        val maxSize = thumbScale * thumbSize / 2
        mStartTouchRect.set(
            spl - maxSize,
            centerY - maxSize,
            spl + maxSize,
            centerY + maxSize
        )
        mEndTouchRect.set(
            spr - maxSize,
            centerY - maxSize,
            spr + maxSize,
            centerY + maxSize
        )
    }

    private fun ValueAnimator?.isRunning(): Boolean {
        return this != null && isStarted && isRunning
    }

    private fun duration() = (mAnimaValue / 1F * 300).toLong()

    private fun setStart(s: Float) {
        mSelectedRect.left = s
        val maxSize = maxThumbSize / 2
        mStartTouchRect.left = s - maxSize
        mStartTouchRect.right = s + maxSize
        val ns = realProgress(s)
        if (ns != start) {
            start = ns
            callback?.invoke(start, end)
        }
    }

    private fun setEnd(e: Float) {
        mSelectedRect.right = e
        val maxSize = thumbScale * thumbSize / 2
        mEndTouchRect.left = e - maxSize
        mEndTouchRect.right = e + maxSize
        val ne = realProgress(e)
        if (ne != end) {
            end = ne
            callback?.invoke(start, end)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        val action: Int = event.actionMasked
        val pointerIndex: Int

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = event.getPointerId(0)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)
                mTouchX = x
                val ts = isTabStart(x, y)
                val te = isTabEnd(x, y)
                mTouchCheck = if (te && ts) {
                    2
                } else if (te) {
                    onStartDrag()
                    1
                } else if (ts) {
                    onStartDrag()
                    0
                } else {
                    -1
                }

                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == -1) return false
                pointerIndex = event.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val x = event.getX(pointerIndex)
                var isHandler = false

                if (mTouchCheck == 2) {
                    val ppx = x - mTouchX
                    if (ppx < 0) {
                        mTouchCheck = 0
                    } else if (ppx > 0) {
                        mTouchCheck = 1
                    }
                    if (mTouchCheck != 2) onStartDrag()
                }

                when (mTouchCheck) {
                    0 -> {

                        if (x in mProgressRect.left..mSelectedRect.right) {
                            isHandler = true
                            setStart(x)
                            requestInvalidate()
                        } else {
                            if (x < mProgressRect.left && mSelectedRect.left != mProgressRect.left) {
                                isHandler = true
                                setStart(mProgressRect.left)
                                requestInvalidate()
                            } else if (x > mSelectedRect.right && mSelectedRect.left != mSelectedRect.right) {
                                isHandler = true
                                setStart(mSelectedRect.right)
                                requestInvalidate()
                            }
                        }
                    }
                    1 -> {
                        if (x in mSelectedRect.left..mProgressRect.right) {
                            isHandler = true
                            setEnd(x)
                            requestInvalidate()
                        } else {

                            if (x < mSelectedRect.left && mSelectedRect.left != mSelectedRect.right) {
                                isHandler = true
                                setEnd(mSelectedRect.left)
                                requestInvalidate()
                            } else if (x > mProgressRect.right && mProgressRect.right != mSelectedRect.right) {
                                isHandler = true
                                setEnd(mProgressRect.right)
                                requestInvalidate()
                            }
                        }
                    }
                }

                if (isHandler) {
                    mTouchX = x
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                onFinishDrag()
                mActivePointerId = -1
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val heightResult = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.UNSPECIFIED,
            MeasureSpec.AT_MOST ->
                ((thumbScale * thumbSize).coerceAtLeast(progressWidth) + paddingBottom + paddingTop).toInt()
            else -> throw IllegalAccessException()
        }

        setMeasuredDimension(widthSize, heightResult)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val centerX = (right - left) / 2
        val centerY = (bottom - top) / 2

        val progressLength = (right - left) - paddingStart - paddingEnd - maxThumbSize
        val pl = centerX - progressLength / 2
        val pr = centerX + progressLength / 2
        val pt = centerY - progressWidth / 2
        val pb = centerY + progressWidth / 2
        mProgressRect.set(pl, pt, pr, pb)

        updateRect()
    }

    override fun onDraw(canvas: Canvas?) {
        mPaint.reset()
        mPaint.isAntiAlias = true

        mPaint.color = progressColor.toInt()
        canvas?.drawRect(mProgressRect, mPaint)

        mPaint.color = selectedColor.toInt()
        canvas?.drawRect(mSelectedRect, mPaint)

        val maxSize = thumbScale * thumbSize / 2

        val spl = mSelectedRect.left
        val spr = mSelectedRect.right
        val centerY = (mSelectedRect.bottom + mSelectedRect.top) / 2

        mStartTouchRect.set(
            spl - maxSize,
            centerY - maxSize,
            spl + maxSize,
            centerY + maxSize
        )
        mEndTouchRect.set(
            spr - maxSize,
            centerY - maxSize,
            spr + maxSize,
            centerY + maxSize
        )

        val currentSize = outerThumbSize / 2

        if (mTouchCheck == 0) {
            mPaint.color = attachAlpha(thumbColor, 0.12f).toInt()
            val ecl = spl - currentSize
            val ecr = spl + currentSize
            val ect = centerY - currentSize
            val ecb = centerY + currentSize
            canvas?.drawOval(ecl, ect, ecr, ecb, mPaint)
        } else if (mTouchCheck == 1) {
            mPaint.color = attachAlpha(thumbColor, 0.12f).toInt()
            val ecl = spr - currentSize
            val ecr = spr + currentSize
            val ect = centerY - currentSize
            val ecb = centerY + currentSize
            canvas?.drawOval(ecl, ect, ecr, ecb, mPaint)
        }

        mPaint.color = thumbColor.toInt()

        val ss = if (mTouchCheck == 0) innerThumbSize / 2 else thumbSize / 2
        val sl = spl - ss
        val sr = spl + ss
        val st = centerY - ss
        val sb = centerY + ss
        canvas?.drawOval(sl, st, sr, sb, mPaint)

        val es = if (mTouchCheck == 1) innerThumbSize / 2 else thumbSize / 2
        val el = spr - es
        val er = spr + es
        val et = centerY - es
        val eb = centerY + es
        canvas?.drawOval(el, et, er, eb, mPaint)
    }
}