package com.mitsuki.ehit.ui.common.widget

import android.graphics.*
import android.widget.ImageView
import androidx.annotation.Px
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil.size.Size
import coil.transform.Transformation

class PreviewTransformation(
    private val target: ImageView,
    @Px private val left: Int,
    @Px private val top: Int,
    @Px private val right: Int,
    @Px private val bottom: Int
) : Transformation {

    override val cacheKey: String =
        "${PreviewTransformation::class.java.name}$left$top$right$bottom"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val targetWidth = target.measuredWidth
        val targetHeight = target.measuredHeight

        val ratio = (right - left).toFloat() / (bottom - top).toFloat()
        val resultWidth: Int
        val resultHeight: Int
        if (ratio > 1) {
            //宽度基准
            resultWidth = targetWidth
            resultHeight = (targetWidth / ratio).toInt()
        } else {
            //高度基准
            resultHeight = targetHeight
            resultWidth = (targetHeight * ratio).toInt()
        }

        val output = createBitmap(targetWidth, targetHeight, input.config ?: Bitmap.Config.ARGB_8888)
        output.applyCanvas {
            drawBitmap(input, Rect(left, top, right, bottom), Rect(0, 0, resultWidth, resultHeight), null)
        }

        return output
    }

    override fun equals(other: Any?): Boolean {
        return other is PreviewTransformation &&
                left == other.left &&
                top == other.top &&
                right == other.right &&
                bottom == other.bottom
    }

    override fun hashCode(): Int {
        var result = left * 31
        result = 31 * result + top
        result = 31 * result + right
        result = 31 * result + bottom
        return result
    }

    override fun toString(): String = "PreviewTransformation"
}