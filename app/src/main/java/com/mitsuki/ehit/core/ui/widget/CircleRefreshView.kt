package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat

class CircleRefreshView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val SHADOW_ELEVATION = 4
    }

    private var mBackgroundColor: Int = -0x50506

    private var mListener: (() -> Unit)? = null

    init {
        getContext().resources.displayMetrics.density.apply {
            ViewCompat.setElevation(this@CircleRefreshView, SHADOW_ELEVATION * this)
        }

        ShapeDrawable(OvalShape()).apply {
            paint.color = mBackgroundColor
            ViewCompat.setBackground(this@CircleRefreshView, this)
        }
    }


    override fun onAnimationEnd() {
        super.onAnimationEnd()
        mListener?.invoke()
    }

    fun setAnimationListener(listener: (() -> Unit)?) {
        mListener = listener
    }

    override fun setBackgroundColor(color: Int) {
        if (background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = color
            mBackgroundColor = color
        }

    }
}
