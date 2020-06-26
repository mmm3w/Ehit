package com.mitsuki.ehit.mvvm.model.entity

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.mvvm.model.ehparser.*
import org.jsoup.nodes.Element

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
) {
    val categoryColor: Int = Category.getColor(category)
    val language: String
    val languageSimple: String

    init {
        with(tag.isNotEmpty() && tag[0].contains("language")) {
            language = if (this) tag[0] else ""
            languageSimple = Language.getLangSimple(language)
        }
    }

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
            ): Boolean =
                oldConcert == newConcert
        }


        fun parse(element: Element): Gallery {

            val glnameEle = element.byClassFirst("glname", "Not found glname")

            val title = glnameEle.byClassFirst("glink", "Not found title").text()

            val url = glnameEle.byTagFirst("a", "Not found url tag").attr("href")
            val tiData = url.splitTI()

            val category = element.byClassFirst("cn", "Not found category").text()

            val time = element.byId("posted_${tiData[0]}", "Not found upload time").text()

            val uploader = element.byClassFirst("glhide", "Not found uploader")
                .byTagFirst("a", "Not found uploader").text()

            val thumb = element.byClassFirst("glthumb", "Not found thumb")
                .byTagFirst("img", "Not found thumb").attr("src")

            val tagElement = element.getElementsByClass("glname")?.first()
                ?.getElementsByClass("gt")

            val tagArray = Array(tagElement?.size ?: 0) { i: Int ->
                tagElement?.get(i)?.attr("title") ?: throw ParseException("parse tag error")
            }

            val rating = element.byClassFirst("ir", "Not found rating").attr("style").parseRating()

            return Gallery(
                tiData[0].toLong(), tiData[1], category, time,
                title, uploader, thumb, tagArray, rating
            )
        }
    }
}