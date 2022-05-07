package com.mitsuki.ehit.ui.detail.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text

class TouchHotspotVisualization @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mLeftRect = Rect()
    private val mTopRect = Rect()
    private val mRightRect = Rect()
    private val mBottomRect = Rect()
    private val mCenterTopRect = Rect()
    private val mCenterBottomRect = Rect()

    private val vp = 0.25F
    private val hp = 0.3F

    private val mPaint = Paint()

    private var mode = 0 //0,1,2

    private var action: (() -> Unit)? = null

    init {
        setOnClickListener {
            mode++
            if (mode >= 3) {
                isVisible = false
                action?.invoke()
                action = null
            } else {
                postInvalidate()
            }
        }
    }

    fun visible(a: () -> Unit) {
        isVisible = true
        action = a
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            mode = 0
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2
        val width = right - left
        val height = bottom - top

        val inLeft = (left + width * hp).toInt()
        val inRight = (right - width * hp).toInt()
        val inTop = (top + height * vp).toInt()
        val inBottom = (bottom - height * vp).toInt()

        mLeftRect.set(left, inTop, inLeft, bottom)
        mTopRect.set(left, top, inRight, inTop)
        mRightRect.set(inRight, top, right, inBottom)
        mBottomRect.set(inLeft, inBottom, right, bottom)
        mCenterTopRect.set(inLeft, inTop, inRight, centerY)
        mCenterBottomRect.set(inLeft, centerY, inRight, inBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(
            0F,
            0F,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            mPaint.color(0x2215b5c1)
        )
        when (mode) {
            0 -> {
                canvas?.drawRect(mLeftRect, mPaint.color(0x8815b5c1.toInt()))
                canvas?.drawRect(mBottomRect, mPaint.color(0x8815b5c1.toInt()))

                mPaint.text(0xffffffff.toInt(), dp2px(20f))
                //TODO 需要根据阅读方向做出调整
                val text = string(R.string.text_next_page)
                val width = mPaint.measureText(text)

                canvas?.drawText(
                    text,
                    mLeftRect.exactCenterX() - width / 2,
                    mLeftRect.exactCenterY(),
                    mPaint
                )
            }
            1 -> {
                canvas?.drawRect(mRightRect, mPaint.color(0x8815b5c1.toInt()))
                canvas?.drawRect(mTopRect, mPaint.color(0x8815b5c1.toInt()))

                mPaint.text(0xffffffff.toInt(), dp2px(20f))
                //TODO 需要根据阅读方向做出调整
                val text = string(R.string.text_previous_page)
                val width = mPaint.measureText(text)

                canvas?.drawText(
                    text,
                    mRightRect.exactCenterX() - width / 2,
                    mRightRect.exactCenterY(),
                    mPaint
                )
            }
            2 -> {
                canvas?.drawRect(mCenterTopRect, mPaint.color(0x8815b5c1.toInt()))
                canvas?.drawRect(mCenterBottomRect, mPaint.color(0x8815b5c1.toInt()))
            }
        }
    }

    private fun Paint.color(color: Int): Paint {
        reset()
        flags = Paint.ANTI_ALIAS_FLAG
        style = Paint.Style.FILL
        setColor(color)
        return this
    }

    private fun Paint.text(color: Int, size: Float): Paint {
        color(color)
        textSize = size
        return this
    }

}