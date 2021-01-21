package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mitsuki.ehit.R

class CategoryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    var mColor: Int = R.color.color_unknow
    var mColorInt = ContextCompat.getColor(context, mColor)

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(mColorInt)
        super.onDraw(canvas)
    }

    fun setCategoryColor(color: Int?) {
        this.mColor = color ?: R.color.color_unknow
        this.mColorInt = ContextCompat.getColor(context, mColor)
    }
}
