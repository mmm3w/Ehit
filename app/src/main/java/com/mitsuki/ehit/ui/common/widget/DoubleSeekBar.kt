package com.mitsuki.ehit.ui.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class DoubleSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    val thumbSize = 80F
    val progressHeight = 10F

    val paint = Paint()

    val min = 0
    val max = 100
    var start = 10
    var end = 90

    var touchCheck: Int = -1

    var downX: Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //判断命中哪个点
                val x = event.x
                val y = event.y
                downX = x
                val ts = isTabInStart(x, y)
                val te = isTabInEnd(x, y)
                touchCheck = if (te && ts) {
                    2
                } else if (te) {
                    1
                } else if (ts) {
                    0
                } else {
                    -1
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val ppx = event.x - downX
                downX = event.x
                if (touchCheck == 2) {
                    touchCheck = if (ppx <= 0) 0 else 1
                }

                when (touchCheck) {
                    0 -> {
                        start += (ppx / (measuredWidth - thumbSize) * (max - min)).roundToInt()
                        start = start.coerceIn(min, end)
                        invalidate()
                    }
                    1 -> {
                        end += (ppx / (measuredWidth - thumbSize) * (max - min)).roundToInt()
                        end = end.coerceIn(start, max)
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                touchCheck = -1
            }
        }
        return true
    }

    private fun isTabInStart(x: Float, y: Float): Boolean {
        return x >= measuredWidth * (start - min).toFloat() / (max - min) &&
                x <= measuredWidth * (start - min).toFloat() / (max - min) + thumbSize &&
                y >= measuredHeight / 2 - thumbSize / 2 &&
                y <= measuredHeight / 2 + thumbSize / 2
    }

    private fun isTabInEnd(x: Float, y: Float): Boolean {
        return x >= measuredWidth - measuredWidth * (max - end).toFloat() / (max - min) - thumbSize &&
                x <= measuredWidth - measuredWidth * (max - end).toFloat() / (max - min) &&
                y >= measuredHeight / 2 - thumbSize / 2 &&
                y <= measuredHeight / 2 + thumbSize / 2
    }

    override fun onDraw(canvas: Canvas?) {
        paint.reset()
        paint.color = 0xFF00ff00.toInt()
        canvas?.drawRect(
            thumbSize / 2,
            measuredHeight / 2 - progressHeight / 2,
            measuredWidth - thumbSize / 2,
            measuredHeight / 2 + progressHeight / 2,
            paint
        )

        paint.color = 0xFFff0000.toInt()
        canvas?.drawRect(
            thumbSize / 2 + measuredWidth * (start - min).toFloat() / (max - min),
            measuredHeight / 2 - progressHeight / 2,
            measuredWidth - measuredWidth * (max - end).toFloat() / (max - min) - thumbSize / 2,
            measuredHeight / 2 + progressHeight / 2,
            paint
        )
        paint.color = 0x440000ff.toInt()
        canvas?.drawOval(
            measuredWidth * (start - min).toFloat() / (max - min),
            measuredHeight / 2 - thumbSize / 2,
            measuredWidth * (start - min).toFloat() / (max - min) + thumbSize,
            measuredHeight / 2 + thumbSize / 2,
            paint
        )
        paint.color = 0x4400fff.toInt()
        canvas?.drawOval(
            measuredWidth - measuredWidth * (max - end).toFloat() / (max - min) - thumbSize,
            measuredHeight / 2 - thumbSize / 2,
            measuredWidth - measuredWidth * (max - end).toFloat() / (max - min),
            measuredHeight / 2 + thumbSize / 2,
            paint
        )
    }

}