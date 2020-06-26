package com.mitsuki.ehit.mvvm.model.ehparser

import com.mitsuki.ehit.mvvm.model.entity.Gallery
import org.jsoup.Jsoup
import java.lang.Exception


object GalleryListParser {
    fun parse(result: String): ArrayList<Gallery> {
        val doc = Jsoup.parse(result)
        val list = ArrayList<Gallery>()
        doc.getElementsByClass("itg").first()?.getElementsByTag("tr")?.let {
            for (element in it) {
                if (element.getElementsByTag("th").size > 0)
                    continue
                try {
                    list.add(Gallery.parse(element))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return list
    }
}