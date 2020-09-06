package com.mitsuki.ehit.core.model.entity

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.being.exception.ParseException
import com.mitsuki.ehit.core.model.ehparser.Matcher

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

    override fun toString(): String {
        return "ImageSource: left($left) top($top) right($right) bottom($bottom) \n url($url)"
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageSource>() {
            override fun areItemsTheSame(
                oldConcert: ImageSource,
                newConcert: ImageSource
            ): Boolean =
                oldConcert.url == newConcert.url

            override fun areContentsTheSame(
                oldConcert: ImageSource,
                newConcert: ImageSource
            ): Boolean {
                return oldConcert.url == newConcert.url
                        && oldConcert.left == newConcert.left
                        && oldConcert.top == newConcert.top
                        && oldConcert.right == newConcert.right
                        && oldConcert.bottom == newConcert.bottom
            }
        }


        fun parse(body: String): PageInfo<ImageSource> {
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
                    list.add(ImageSource(it.group(3), left, top, left + width, top + height))
                }
            }

            val totalSize = Matcher.PAGER_TOTAL_SIZE.matcher(body).run {
                if (find()) {
                    group(1).toIntOrNull() ?: throw ParseException()
                } else throw ParseException()
            }

            var prevKey: Int? = null
            var nextKey: Int? = null
            var index: Int = -1

            Matcher.PAGER_INFO.matcher(body).also {
                if (it.find()) {
                    prevKey = (it.group(1) ?: "").toIntOrNull()
                    nextKey = (it.group(6) ?: "").toIntOrNull()
                    index = (it.group(3) ?: throw  ParseException("未找到当前页码")).toIntOrNull()
                        ?: throw ParseException("当前页码格式转换失败")
                } else throw ParseException("未找到页码信息")
            }

            return PageInfo(list, index.dec(), totalSize, 0, prevKey?.dec(), nextKey?.dec())
        }

        private fun String.spToInt(): Int = this.replace(",", "").toInt()
    }
}