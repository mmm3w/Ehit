package com.mitsuki.ehit.ui.common.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.addListener
import kotlin.math.roundToInt

class DoubleSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var thumbSize = 40f
    private var thumbScale = 2f
    private var thumbColor: Int = 0xffff0000.toInt()
    private var progressColor: Int = 0xffebebeb.toInt()
    private var selectedColor: Int = 0xFFff0000.toInt()
    private var progressWidth = 10f

    private var max = 300
    private var min = 0
    private var start = 0
    private var end = 300

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


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        val action: Int = event.actionMasked
        val pointerIndex: Int

        //多点触控处理
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (mUpAnimation.isRunning()) return false
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
                    startDownAnimation()
                    1
                } else if (ts) {
                    startDownAnimation()
                    0
                } else {
                    -1
                }

                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == -1) return false;
                pointerIndex = event.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val x = event.getX(pointerIndex)
                val ppx = x - mTouchX
                var isHandler = false

                //根据方向选择实际的点
                if (mTouchCheck == 2) {
                    if (ppx < 0) {
                        mTouchCheck = 0
                    } else if (ppx > 0) {
                        mTouchCheck = 1
                    }
                    if (mTouchCheck != 2) startDownAnimation()
                }

                when (mTouchCheck) {
                    0 -> {
                        val ns =
                            ((x - mProgressRect.left) / mProgressRect.width() * (max - min) + min).roundToInt()
                        if (ns in min..end) {
                            isHandler = true
                            start = ns
                            requestInvalidate()
                        } else {
                            if (ns < min && start != min) {
                                isHandler = true
                                start = min
                                requestInvalidate()
                            } else if (ns > end && start != end) {
                                isHandler = true
                                start = end
                                requestInvalidate()
                            }
                        }
                    }
                    1 -> {
                        val ne =
                            ((x - mProgressRect.left) / mProgressRect.width() * (max - min) + min).roundToInt()
                        if (ne in start..max) {
                            isHandler = true
                            end = ne
                            requestInvalidate()
                        } else {
                            if (ne < start && end != start) {
                                isHandler = true
                                end = start
                                requestInvalidate()
                            } else if (ne > max && end != max) {
                                isHandler = true
                                end = max
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
                mActivePointerId = -1
                mTouchCheck = -1
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
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

    private fun requestInvalidate() {
        if (!mHasAnimation) invalidate()
    }

    private fun startDownAnimation() {
        mDownAnimation?.cancel()
        mDownAnimation = ValueAnimator.ofFloat(0f, 1f)
        mDownAnimation?.duration = 300
        mDownAnimation?.addUpdateListener {
            mAnimaValue = it?.animatedValue as Float
            postInvalidateOnAnimation()
        }
        mDownAnimation?.addListener(
            onStart = { mHasAnimation = true },
            onCancel = { mHasAnimation = false },
            onEnd = {
                mHasAnimation = false
                mAnimaValue = 1f
            }

        )
        mDownAnimation?.start()
    }

    private fun startUpAnimation() {

    }

    private fun ValueAnimator?.isRunning(): Boolean {
        return this != null && isStarted && isRunning
    }

    override fun onDraw(canvas: Canvas?) {
        mPaint.reset()
        mPaint.isAntiAlias = true

        val centerX = measuredWidth / 2
        val centerY = measuredHeight / 2

        //绘制整条进度
        val progressLength = measuredWidth - paddingStart - paddingEnd - thumbSize * thumbScale
        val pl = centerX - progressLength / 2
        val pr = centerX + progressLength / 2
        val pt = centerY - progressWidth / 2
        val pb = centerY + progressWidth / 2
        mProgressRect.set(pl, pt, pr, pb)
        mPaint.color = progressColor
        canvas?.drawRect(mProgressRect, mPaint)

        //绘制选中进度
        mPaint.color = selectedColor
        val lp = (start - min) / (max - min).toFloat()
        val rp = (end - min) / (max - min).toFloat()
        val spl = pl + lp * progressLength
        val spr = pl + rp * progressLength
        mSelectedRect.set(spl, pt, spr, pb)
        canvas?.drawRect(mSelectedRect, mPaint)

        //绘制两端控制点
        val ss = if (mTouchCheck == 0) thumbSize / 2 * mAnimaValue * thumbScale else 0F
        mPaint.color = thumbColor
        val sl = spl - thumbSize / 2
        val sr = spl + thumbSize / 2
        val st = centerY - thumbSize / 2
        val sb = centerY + thumbSize / 2
        mStartTouchRect.set(
            sl - thumbScale * thumbSize / 2,
            st - thumbScale * thumbSize / 2,
            sr + thumbScale * thumbSize / 2,
            sb + thumbScale * thumbSize / 2
        )
        canvas?.drawOval(
            sl - ss,
            st - ss,
            sr + ss,
            sb + ss,
            mPaint
        )


        val es = if (mTouchCheck == 1) thumbSize / 2 * mAnimaValue * thumbScale else 0F
        val el = spr - thumbSize / 2
        val er = spr + thumbSize / 2
        val et = centerY - thumbSize / 2
        val eb = centerY + thumbSize / 2
        mEndTouchRect.set(
            el - thumbScale * thumbSize / 2,
            et - thumbScale * thumbSize / 2,
            er + thumbScale * thumbSize / 2,
            eb + thumbScale * thumbSize / 2
        )
        canvas?.drawOval(
            el - es,
            et - es,
            er + es,
            eb + es,
            mPaint
        )


//        if (mTouchCheck == 0) {
//            mPaint.color = thumbColor / 2
//            val ecl = spl - (thumbSize * mAnimaValue * thumbScale) / 2
//            val ecr = spl + (thumbSize * mAnimaValue * thumbScale) / 2
//            val ect = centerY - (thumbSize * mAnimaValue * thumbScale) / 2
//            val ecb = centerY + (thumbSize * mAnimaValue * thumbScale) / 2
//            canvas?.drawOval(ecl, ect, ecr, ecb, mPaint)
//        } else if (mTouchCheck == 1) {
//            mPaint.color = thumbColor / 2
//            val ecl = spr - (thumbSize * mAnimaValue * thumbScale) / 2
//            val ecr = spr + (thumbSize * mAnimaValue * thumbScale) / 2
//            val ect = centerY - (thumbSize * mAnimaValue * thumbScale) / 2
//            val ecb = centerY + (thumbSize * mAnimaValue * thumbScale) / 2
//            canvas?.drawOval(ecl, ect, ecr, ecb, mPaint)
//        }


    }

}