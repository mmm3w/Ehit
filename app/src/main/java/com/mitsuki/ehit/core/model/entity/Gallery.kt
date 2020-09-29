package com.mitsuki.ehit.core.model.entity

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.being.exception.ParseException
import com.mitsuki.ehit.core.model.ehparser.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.Exception

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
    val rating: Float
) : Parcelable {
    val categoryColor: Int = Category.getColor(category)
    val language: String
    val languageSimple: String

    init {
        with(tag.isNotEmpty() && tag[0].contains("language")) {
            language = if (this) tag[0] else ""
            languageSimple = Language.getLangSimple(language)
        }
    }

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
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } ?: ArrayList()
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun parse(element: Element): Gallery {
            val glnameEle = element.byClassFirst("glname", "Not found glname")

            val title = glnameEle.byClassFirst("glink", "Not found title").text()

            val url = glnameEle.byTagFirst("a", "Not found url tag").attr("href")
            val tiData = url.splitIdToken()

            val category = element.byClassFirst("cn", "Not found category").text()

            val time = element.byId("posted_${tiData[0]}", "Not found upload time").text()

            val uploader = element.byClassFirst("glhide", "Not found uploader")
                .byTagFirst("a", "Not found uploader").text()

            val thumbNode = element.byClassFirst("glthumb", "Not found thumb")
                .byTagFirst("img", "Not found thumb")

            val thumb = thumbNode.attr("data-src").let {
                if (it.startsWith("https")) it
                else thumbNode.attr("src")
            }

            val tagElement = element.getElementsByClass("glname")?.first()
                ?.getElementsByClass("gt")

            val tagArray = Array(tagElement?.size ?: 0) { i: Int ->
                tagElement?.get(i)?.attr("title") ?: throw ParseException(
                    "parse tag error"
                )
            }

            val rating = element.byClassFirst("ir", "Not found rating").attr("style").parseRating()

            return Gallery(
                tiData[0].toLong(), tiData[1], category, time,
                title, uploader, thumb, tagArray, rating
            )
        }
    }


}