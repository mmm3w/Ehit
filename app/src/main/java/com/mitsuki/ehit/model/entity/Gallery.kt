package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import com.mitsuki.ehit.crutch.extensions.*
import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import com.mitsuki.ehit.model.ehparser.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Pattern

@Suppress("ArrayInDataClass")
@Parcelize
data class Gallery(
    val gid: Long,
    val token: String,
    val category: String = "Unknown",
    val time: String,
    val title: String,
    val uploader: String,
    val thumb: String,
    val tag: Array<String>,
    val rating: Float,
    val page: Int,
    val categoryColor: Int = Category.getColor(category)
) : Parcelable {

    constructor(gid: Long, token: String) :
            this(gid, token, "", "", "", "", "", emptyArray(), 0F, 0)

    @IgnoredOnParcel
    val language: String
        get() = (tag.isNotEmpty() && tag[0].contains("language")).run {
            if (this) tag[0] else ""
        }

    @IgnoredOnParcel
    val languageSimple: String
        get() = Language.getLangSimple(language)


    @IgnoredOnParcel
    val itemTransitionName = "gallery:$gid$token"

    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        fun parseList(content: String?): PageInfo<Gallery> {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")

            val doc = Jsoup.parse(content)

            val listData = ArrayList<Gallery>().apply {
                doc.getElementsByClass("itg").first()?.getElementsByTag("tr")?.let {
                    for (element in it) {
                        if (element.getElementsByTag("th").size > 0)
                            continue
                        try {
                            add(parse(element))
                        } catch (inner: Throwable) {
                            inner.printStackTrace()
                        }
                    }
                }
            }

            val ptt: Element? = doc.byClassFirstIgnoreError("ptt")
            val totalPage =
                ptt?.child(0)?.child(0)?.children()?.let { it[it.size - 2].text().trim().toInt() }
                    ?: 0

            val totalCount = Matcher.LIST_TOTAL_COUNT.matcher(content).run {
                if (find()) {
                    group(1)
                        ?.split(",")
                        ?.joinToString(separator = "")
                        ?.toIntOrNull()
                        ?: throw ParseThrowable("total count parse error")
                } else 0
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
                }
//                else throw ParseThrowable("未找到页码信息")
            }

            return PageInfo(
                listData,
                index.dec(),
                totalCount,
                totalPage,
                prevKey?.dec(),
                nextKey?.dec()
            )
        }

        fun parseExList(content: String?): PageInfoNew<Gallery> {
            if (content.isNullOrEmpty()) throw ParseThrowable("未请求到数据")

            val doc = Jsoup.parse(content)

            val listData = ArrayList<Gallery>().apply {
                doc.getElementsByClass("itg").first()?.getElementsByTag("tr")?.let {
                    for (element in it) {
                        if (element.getElementsByTag("th").size > 0)
                            continue
                        try {
                            add(parse(element))
                        } catch (inner: Throwable) {
                            inner.printStackTrace()
                        }
                    }
                }
            }

//            val totalNode: Element? = doc.byClassFirstIgnoreError("searchtext")
//            val totalCount =
//                totalNode?.html()?.let { Matcher.EX_LIST_TOTAL_COUNT.matcher(it) }?.run {
//                    if (find()) {
//                        group(2)
//                            ?.split(",")
//                            ?.joinToString(separator = "")
//                            ?.toIntOrNull()
//                            ?: throw ParseThrowable("total count parse error")
//                    } else 0
//                } ?: 0

            val pageNode = doc.byClassFirstIgnoreError("searchnav")
            val nextNode = pageNode?.getElementById("unext")
            val prevNode = pageNode?.getElementById("uprev")

            val prevKey: Long? =
                prevNode?.toString()?.let { Matcher.EX_PAGE_PREV.matcher(it) }?.run {
                    if (find()) group(1)?.toLongOrNull() else null
                }
            val nextKey: Long? =
                nextNode?.toString()?.let { Matcher.EX_PAGE_NEXT.matcher(it) }?.run {
                    if (find()) group(1)?.toLongOrNull() else null
                }

            return PageInfoNew(
                listData,
                0,
                "",
                prevKey,
                nextKey
            )
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parse(element: Element): Gallery {
            val glnameEle = element.byClassFirst("glname", "glname node".prefix())

            val title = glnameEle.byClassFirst("glink", "title (glink node text)".prefix()).text()

            val url = glnameEle.byTagFirst("a", "url (glname node a href)".prefix()).attr("href")

            val tiData = url.splitIdToken()

            val category = element.byClassFirst("cn", "category (cn node text)".prefix()).text()

            val time = element.getElementById("posted_${tiData[0]}")?.text()
                ?: throw ParseThrowable("upload time (posted_${tiData[0]} node text)".prefix())

            val glhideNode = element.byClassFirstIgnoreError("glhide")?.text()?.split(Matcher.SPACE)
            val uploader = glhideNode?.let { if (it.size > 2) it[0] else "" } ?: ""
            val page = glhideNode?.let { if (it.size > 2) it[1].toIntOrNull() else 0 } ?: 0


            val thumbNode = element
                .byClassFirst("glthumb", "thumbNode (glthumb node)".prefix())
                .byTagFirst("img", "thumbNode (glthumb node img)".prefix())

            val thumb = thumbNode.attr("data-src").let {
                if (it.startsWith("https")) it
                else thumbNode.attr("src")
            }

            val tagElement = element.getElementsByClass("glname")?.first()
                ?.getElementsByClass("gt")

            val tagArray = Array(tagElement?.size ?: 0) { i: Int ->
                tagElement?.get(i)?.attr("title") ?: throw ParseThrowable("parse tag error")
            }

            val rating = element
                .byClassFirst("ir", "rating (ir node style)".prefix())
                .attr("style")
                .parseRating()

            return Gallery(
                tiData[0].toLong(), tiData[1], category, time,
                title, uploader, thumb, tagArray, rating, page
            )
        }

        private fun String.prefix(): String = "Parse gallery item: not found $this"
    }

}