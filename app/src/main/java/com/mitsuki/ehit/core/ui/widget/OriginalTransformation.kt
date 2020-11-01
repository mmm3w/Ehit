package com.mitsuki.ehit.core.ui.widget

import android.graphics.Bitmap
import androidx.core.graphics.applyCanvas
import coil.bitmap.BitmapPool
import coil.size.OriginalSize
import coil.size.Size
import coil.transform.Transformation

class OriginalTransformation : Transformation {
    override fun key(): String {
        return javaClass.name
    }

    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        if (size !is OriginalSize) throw  Exception("should be original size")
        return pool.get(input.width, input.height, input.config)
            .applyCanvas { drawBitmap(input, 0f, 0f, null) }
    }
}

