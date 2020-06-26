package com.mitsuki.ehit.mvvm.model.ehparser

import com.mitsuki.ehit.R

object Category {
    val CATEGORY_SET = arrayOf(
        "Doujinshi", "Manga", "Artist CG",
        "Game CG", "Western", "Non-H",
        "Image Set", "Cosplay", "Asian Porn",
        "Misc", "Unknow"
    )
    val CATEGORY_COLOR = arrayOf(
        R.color.color_doujinshi, R.color.color_manga, R.color.color_artist_cg,
        R.color.color_game_cg, R.color.color_western, R.color.color_non_h,
        R.color.color_image_set, R.color.color_cosplay, R.color.color_asian_porn,
        R.color.color_misc, R.color.color_unknow
    )

    fun getColor(c: String): Int {
        for (i in CATEGORY_SET.indices) {
            if (CATEGORY_SET[i].equals(c, ignoreCase = true)) {
                return CATEGORY_COLOR[i]
            }
        }
        return R.color.color_unknow
    }

}