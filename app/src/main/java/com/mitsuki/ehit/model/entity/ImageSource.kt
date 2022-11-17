package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import com.mitsuki.ehit.model.ehparser.Matcher
import com.mitsuki.ehit.crutch.extensions.htmlEscape
import com.mitsuki.ehit.crutch.network.ehcore.ApiContainer
import com.mitsuki.ehit.model.entity.db.GalleryImageSourceCache
import java.util.regex.Pattern

data class ImageSource(
    val imageUrl: String,
    val index: Int,
    val pageUrl: String,
    val pToken: String,
    val left: Int = -1,
    val top: Int = -1,
    val right: Int = -1,
    val bottom: Int = -1
) {
    constructor(cache: GalleryImageSourceCache) : this(
        cache.imageUrl,
        cache.index,
        cache.pageUrl,
        cache.pToken,
        cache.left,
        cache.top,
        cache.right,
        cache.bottom
    )

    override fun toString(): String {
        return "ImageSource: left($left) top($top) right($right) bottom($bottom) \n url($imageUrl)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ImageSource &&
                imageUrl == other.imageUrl &&
                index == other.index &&
                pageUrl == other.pageUrl &&
                pToken == other.pToken &&
                left == other.left &&
                top == other.top &&
                right == other.right &&
                bottom == other.bottom
    }

    override fun hashCode(): Int {
        var result = imageUrl.hashCode()
        result = 31 * result + index
        result = 31 * result + pageUrl.hashCode()
        result = 31 * result + pToken.hashCode()
        result = 31 * result + left
        result = 31 * result + top
        result = 31 * result + right
        result = 31 * result + bottom
        return result
    }

    companion object {

        fun parse(content: String?): PageInfo<ImageSource> {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")
            val list = parseWithNormal(content)
            if (list.isEmpty()) list.addAll(parseWithLarge(content))
            if (list.isEmpty()) throw ParseThrowable("lost total size")

            val totalSize = Matcher.PAGER_TOTAL_SIZE.matcher(content).run {
                if (find()) {
                    group(1)?.toIntOrNull() ?: throw ParseThrowable("lost total size")
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

        private fun parseWithNormal(content: String?): ArrayList<ImageSource> {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")

            val tokenRegex = Pattern.compile("${ApiContainer.galleryList()}s/([0-9a-f]{10})/(\\d+)-(\\d+)")

            return ArrayList<ImageSource>().apply {
                Matcher.NORMAL_PREVIEW.matcher(content).also {
                    while (it.find()) {
                        val index = it.group(6).spToInt() - 1
                        if (index < 0) continue
                        val width = it.group(1).spToInt()
                        if (width <= 0) continue
                        val height = it.group(2).spToInt()
                        if (height <= 0) continue
                        val left = it.group(4).spToInt()
                        val top = 0

                        val pageUrl = it.group(5)?.trim()?.htmlEscape() ?: ""
                        val pToken = tokenRegex.matcher(pageUrl).run {
                            if (find()) group(1) else throw ParseThrowable("lost page token")
                        }
                        add(
                            ImageSource(
                                it.group(3) ?: "", index, pageUrl, pToken,
                                left, top, left + width, top + height
                            )
                        )
                    }
                }
            }
        }

        private fun parseWithLarge(content: String?): MutableList<ImageSource> {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")

            val tokenRegex = Pattern.compile("${ApiContainer.galleryList()}s/([0-9a-f]{10})/(\\d+)-(\\d+)")
            return ArrayList<ImageSource>().apply {
                Matcher.LARGE_PREVIEW.matcher(content).also {
                    while (it.find()) {
                        val index: Int = it.group(2).spToInt() - 1
                        if (index < 0) continue
                        val pageUrl = it.group(1)?.trim()?.htmlEscape() ?: ""
                        val pToken = tokenRegex.matcher(pageUrl).run {
                            if (find()) group(1) else throw ParseThrowable("lost page token")
                        }
                        val imageUrl = it.group(3)?.trim()?.htmlEscape() ?: ""
                        add(ImageSource(imageUrl, index, pageUrl, pToken))
                    }
                }
            }
        }


        private fun String?.spToInt(): Int = this?.replace(",", "")?.toIntOrNull() ?: 0
    }
}