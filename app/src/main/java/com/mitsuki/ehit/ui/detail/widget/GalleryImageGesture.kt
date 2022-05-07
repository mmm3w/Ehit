package com.mitsuki.ehit.ui.detail.widget

import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.mitsuki.armory.imagegesture.ImageGesture

class GalleryImageGesture(imageView: ImageView) : ImageGesture(imageView) {

    var onLongPress: (() -> Unit)? = null

    var onAreaTap: ((Int) -> Unit)? = null

    private val mLeftRect = RectF()
    private val mTopRect = RectF()
    private val mRightRect = RectF()
    private val mBottomRect = RectF()
    private val mCenterTopRect = RectF()
    private val mCenterBottomRect = RectF()

    private val vp = 0.25F
    private val hp = 0.3F

    override fun onLongPress(e: MotionEvent) {
        onLongPress?.invoke()
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val tapX = e.x
        val tapY = e.y

        when {
            mLeftRect.contains(tapX, tapY) -> onAreaTap?.invoke(0)
            mTopRect.contains(tapX, tapY) -> onAreaTap?.invoke(1)
            mRightRect.contains(tapX, tapY) -> onAreaTap?.invoke(2)
            mBottomRect.contains(tapX, tapY) -> onAreaTap?.invoke(3)
            mCenterTopRect.contains(tapX, tapY) -> onAreaTap?.invoke(4)
            mCenterBottomRect.contains(tapX, tapY) -> onAreaTap?.invoke(5)
            else -> return false
        }
        return true
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        super.onLayoutChange(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
        val centerY = (top + bottom) / 2F
        val width = right - left
        val height = bottom - top

        val inLeft = left + width * hp
        val inRight = right - width * hp
        val inTop = top + height * vp
        val inBottom = bottom - height * vp

        mLeftRect.set(left.toFloat(), inTop, inLeft, bottom.toFloat())
        mTopRect.set(left.toFloat(), top.toFloat(), inRight, inTop)
        mRightRect.set(inRight, top.toFloat(), right.toFloat(), inBottom)
        mBottomRect.set(inLeft, inBottom, right.toFloat(), bottom.toFloat())
        mCenterTopRect.set(inLeft, inTop, inRight, centerY)
        mCenterBottomRect.set(inLeft, centerY, inRight, inBottom)
    }

}