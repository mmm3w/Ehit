package com.mitsuki.ehit.model.entity

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.crutch.Log
import com.mitsuki.ehit.crutch.throwable.ParseThrowable
import com.mitsuki.ehit.model.ehparser.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

@Suppress("ArrayInDataClass")
@Parcelize
data class Gallery(
    val gid: Long,
    val token: String,
    val category: String = "Unknow",
    val time: String,
    val title: String,
    val uploader: String,
    val thumb: String,
    val tag: Array<String>,
    val rating: Float,
    val categoryColor: Int = Category.getColor(category)
) : Parcelable {

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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Gallery>() {
            override fun areItemsTheSame(
                oldConcert: Gallery,
                newConcert: Gallery
            ): Boolean =
                oldConcert.gid == newConcert.gid

            override fun areContentsTheSame(
                oldConcert: Gallery,
                newConcert: Gallery
            ): Boolean {
                return oldConcert.token == newConcert.token
                        && oldConcert.category == newConcert.category
                        && oldConcert.time == newConcert.time
                        && oldConcert.title == newConcert.title
                        && oldConcert.uploader == newConcert.uploader
                        && oldConcert.thumb == newConcert.thumb
                        && oldConcert.rating == newConcert.rating
                        && oldConcert.language == newConcert.language
            }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parseList(content: String?): ArrayList<Gallery> {
            return content?.run {
                ArrayList<Gallery>().apply {
                    val doc = Jsoup.parse(content)
                    doc.getElementsByClass("itg").first()?.getElementsByTag("tr")?.let {
                        for (element in it) {
                            if (element.getElementsByTag("th").size > 0)
                                continue
                            try {
                                add(parse(element))
                            } catch (inner: Throwable) {
                                Log.debug("${inner.message}\n$element")
                            }
                        }
                    }
                }
            } ?: ArrayList()
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

            val uploader = element
                .byClassFirst("glhide", "uploader (glhide node)".prefix())
                .byTagFirst("a", "uploader (glhide node a text)".prefix())
                .text()

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
                title, uploader, thumb, tagArray, rating
            )
        }

        private fun String.prefix(): String = "Parse gallery item: not found $this"
    }


    fun obtainHeader() = GalleryDetailWrap.DetailHeader(thumb, title, uploader, category)
}