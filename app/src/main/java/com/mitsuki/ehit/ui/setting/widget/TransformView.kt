package com.mitsuki.ehit.ui.setting.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.animation.addListener

@SuppressLint("ViewConstructor")
class TransformView(context: Context, private val bitmap: Bitmap) : View(context) {

    private var mCurrent: Float = 0f

    companion object {
        const val DURATION = 300L
    }

    fun start(activity: Activity) {
        val decorView = activity.window.decorView as FrameLayout
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        decorView.addView(this)
        bringToFront()
        ValueAnimator.ofFloat(0f, decorView.width.toFloat())
            .setDuration(DURATION)
            .apply {
                addUpdateListener {
                    mCurrent = (it.animatedValue as? Float)!!
                    postInvalidate()
                }
                addListener(onEnd = {
                    decorView.removeView(this@TransformView)
                    bitmap.takeIf { !it.isRecycled }?.recycle()
                })
            }
            .start()

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.clipRect(mCurrent, 0f, width.toFloat(), height.toFloat())
        bitmap.takeIf { !it.isRecycled }?.also { bitmap ->
            canvas?.drawBitmap(bitmap, 0f, 0f, null)
        }
    }

}