package com.mitsuki.ehit.core.model.entity

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.being.throwable.ParseThrowable
import com.mitsuki.ehit.being.throwable.assertContent
import com.mitsuki.ehit.core.model.ehparser.Matcher
import com.mitsuki.ehit.core.model.ehparser.htmlEscape

@Suppress(
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
data class ImageSource(
    val imageUrl: String,
    val index: Int,
    val pageUrl: String,
    val pToken: String,
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width = right - left
    val height = bottom - top

    override fun toString(): String {
        return "ImageSource: left($left) top($top) right($right) bottom($bottom) \n url($imageUrl)"
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ImageSource>() {
            override fun areItemsTheSame(
                oldConcert: ImageSource,
                newConcert: ImageSource
            ): Boolean =
                oldConcert.imageUrl == newConcert.imageUrl

            override fun areContentsTheSame(
                oldConcert: ImageSource,
                newConcert: ImageSource
            ): Boolean {
                return oldConcert.imageUrl == newConcert.imageUrl
                        && oldConcert.index == newConcert.index
                        && oldConcert.pageUrl == newConcert.pageUrl
                        && oldConcert.pToken == newConcert.pToken
                        && oldConcert.left == newConcert.left
                        && oldConcert.top == newConcert.top
                        && oldConcert.right == newConcert.right
                        && oldConcert.bottom == newConcert.bottom
            }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parseWithNormal(content: String?): List<ImageSource> {
            assertContent(content,"")
            return ArrayList<ImageSource>().apply {
                Matcher.NORMAL_PREVIEW.matcher(content).also {
                    while (it.find()) {
                        val index = it.group(6).spToInt() - 1
                        if (index < 0)
                            continue
                        val width = it.group(1).spToInt()
                        if (width <= 0) continue
                        val height = it.group(2).spToInt()
                        if (height <= 0) continue
                        val left = it.group(4).spToInt()
                        val top = 0

                        val pageUrl = it.group(5).trim().htmlEscape()
                        val pToken = Matcher.PREVIEW_PAGE_TO_TOKEN.matcher(pageUrl).run {
                            if (find()) group(1) else throw ParseThrowable("lost page token")
                        }
                        add(
                            ImageSource(
                                it.group(3), index, pageUrl, pToken,
                                left, top, left + width, top + height
                            )
                        )
                    }
                }
            }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parseWithLarge(content: String?): List<ImageSource> {
            assertContent(content,"")
            return ArrayList<ImageSource>()
        }

        fun parse(content: String?): PageInfo<ImageSource> {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")
            val list = try {
                parseWithNormal(content)
            } catch (e: Exception) {
                parseWithNormal(content)
            }

            val totalSize = Matcher.PAGER_TOTAL_SIZE.matcher(content).run {
                if (find()) {
                    group(1).toIntOrNull() ?: throw ParseThrowable("lost total size")
                } else throw ParseThrowable("lost total size")
            }

            var prevKey: Int? = null
            var nextKey: Int? = null
            var index: Int = -1

            Matcher.PAGER_INFO.matcher(content).also {
                if (it.find()) {
                    prevKey = (it.group(1) ?: "").toIntOrNull()
                    nextKey = (it.group(6) ?: "").toIntOrNull()
                    index = (it.group(3) ?: throw  ParseThrowable("未找到当前页码")).toIntOrNull()
                        ?: throw ParseThrowable("当前页码格式转换失败")
                } else throw ParseThrowable("未找到页码信息")
            }

            return PageInfo(list, index.dec(), totalSize, 0, prevKey?.dec(), nextKey?.dec())
        }

        private fun String.spToInt(): Int = this.replace(",", "").toIntOrNull() ?: 0
    }
}