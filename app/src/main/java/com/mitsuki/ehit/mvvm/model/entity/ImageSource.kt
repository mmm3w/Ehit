package com.mitsuki.ehit.mvvm.model.entity

import android.graphics.Rect
import com.mitsuki.ehit.mvvm.model.ehparser.Matcher

@Suppress(
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
data class ImageSource(
    val url: String,
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width = right - left
    val height = bottom - top

    companion object {
        fun parse(body: String): ArrayList<ImageSource> {
            val list = ArrayList<ImageSource>()

            Matcher.NORMAL_PREVIEW.matcher(body).also {
                while (it.find()) {
                    if (it.group(6).spToInt() < 1)
                        continue
                    val width = it.group(1).spToInt()
                    if (width <= 0) continue
                    val height = it.group(2).spToInt()
                    if (height <= 0) continue
                    val left = it.group(4).spToInt()
                    val top = 0
                    list.add(ImageSource(it.group(5), left, top, left + width, top + height))
                }
            }
            return list
        }

        private fun String.spToInt(): Int = this.replace(",", "").toInt()
    }
}