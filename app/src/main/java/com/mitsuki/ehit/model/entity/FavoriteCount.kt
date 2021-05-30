package com.mitsuki.ehit.model.entity

import com.mitsuki.ehit.crutch.extend.saveToInt
import org.jsoup.Jsoup

class FavoriteCount {
    companion object {
        fun parse(content: String): Array<Int> {
            val doc = Jsoup.parse(content)
            val fpNodes = doc.getElementsByClass("fp")
            if (fpNodes.size != 11) return Array(10) { 0 }
            return Array(10) { fpNodes[it].child(0).text().saveToInt() }
        }
    }
}