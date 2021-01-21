package com.mitsuki.ehit.core.model.ehparser

import com.mitsuki.ehit.R

object Category {
    val DATA = arrayOf(
        "Doujinshi" to R.color.color_doujinshi,
        "Manga" to R.color.color_manga,
        "Artist CG" to R.color.color_artist_cg,
        "Game CG" to R.color.color_game_cg,
        "Western" to R.color.color_western,
        "Non-H" to R.color.color_non_h,
        "Image Set" to R.color.color_image_set,
        "Cosplay" to R.color.color_cosplay,
        "Asian Porn" to R.color.color_asian_porn,
        "Misc" to R.color.color_misc,
        "Unknow" to R.color.color_unknow
    )

    fun getColor(c: String): Int {
        for (item in DATA) {
            if (item.first.equals(c, ignoreCase = true)) {
                return item.second
            }
        }
        return R.color.color_unknow
    }

    fun color(index: Int): Int {
        return DATA[index].second
    }

    fun text(index: Int): String {
        return DATA[index].first
    }
}