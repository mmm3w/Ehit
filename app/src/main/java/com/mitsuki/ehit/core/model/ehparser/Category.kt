package com.mitsuki.ehit.core.model.ehparser

import androidx.annotation.ColorRes
import com.mitsuki.ehit.R

object Category {

    const val UNKNOWN_CODE = -1
    const val ALL_CATEGORY = 0x3ff

    val DATA = arrayOf(
        CategoryMeta("Doujinshi", R.color.color_doujinshi, 0x2),
        CategoryMeta("Manga", R.color.color_manga, 0x4),
        CategoryMeta("Artist CG", R.color.color_artist_cg, 0x8),
        CategoryMeta("Game CG", R.color.color_game_cg, 0x10),
        CategoryMeta("Western", R.color.color_western, 0x200),
        CategoryMeta("Non-H", R.color.color_non_h, 0x100),
        CategoryMeta("Image Set", R.color.color_image_set, 0x20),
        CategoryMeta("Cosplay", R.color.color_cosplay, 0x40),
        CategoryMeta("Asian Porn", R.color.color_asian_porn, 0x80),
        CategoryMeta("Misc", R.color.color_misc, 0x1),
        CategoryMeta("Unknow", R.color.color_unknow, UNKNOWN_CODE)
    )

    fun getColor(c: String): Int {
        for (item in DATA) {
            if (item.text.equals(c, ignoreCase = true)) {
                return item.res
            }
        }
        return R.color.color_unknow
    }

    fun color(index: Int): Int {
        return DATA[index].res
    }

    fun text(index: Int): String {
        return DATA[index].text
    }

    data class CategoryMeta(val text: String, @ColorRes val res: Int, val code: Int)
}