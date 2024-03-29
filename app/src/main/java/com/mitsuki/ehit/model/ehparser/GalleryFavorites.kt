package com.mitsuki.ehit.model.ehparser

import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.saveToInt
import com.mitsuki.ehit.crutch.extensions.string
import org.jsoup.Jsoup

object GalleryFavorites {
    private const val FAV_CAT_0 = "Favorites 0"
    private const val FAV_CAT_1 = "Favorites 1"
    private const val FAV_CAT_2 = "Favorites 2"
    private const val FAV_CAT_3 = "Favorites 3"
    private const val FAV_CAT_4 = "Favorites 4"
    private const val FAV_CAT_5 = "Favorites 5"
    private const val FAV_CAT_6 = "Favorites 6"
    private const val FAV_CAT_7 = "Favorites 7"
    private const val FAV_CAT_8 = "Favorites 8"
    private const val FAV_CAT_9 = "Favorites 9"

    val favorites = arrayListOf(
        FAV_CAT_0,
        FAV_CAT_1,
        FAV_CAT_2,
        FAV_CAT_3,
        FAV_CAT_4,
        FAV_CAT_5,
        FAV_CAT_6,
        FAV_CAT_7,
        FAV_CAT_8,
        FAV_CAT_9
    )

    fun findIndex(name: String?): Int {
        if (name == null) return -1
        return favorites.indexOf(name)
    }

    fun findName(index: Int): String? {
        if (index < 0) return null
        if (index >= favorites.size) return null
        return favorites[index]
    }

    fun parse(content: String): Array<Int> {
        val doc = Jsoup.parse(content)
        val fpNodes = doc.getElementsByClass("fp")
        if (fpNodes.size != 11) return Array(10) { 0 }
        return Array(10) { fpNodes[it].child(0).text().saveToInt() }
    }

    fun attachName(countData: Array<Int>): Array<Pair<String, Int>> {
        var total = 0
        countData.forEach { total += it }
        val data = favorites.mapIndexed { index: Int, str: String ->
            try {
                str to countData[index]
            } catch (inner: Throwable) {
                str to 0
            }
        }
        return Array(11) { if (it == 0) string(R.string.text_all) to total else data[it - 1] }
    }
}