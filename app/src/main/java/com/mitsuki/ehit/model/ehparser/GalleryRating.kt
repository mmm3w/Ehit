package com.mitsuki.ehit.model.ehparser

import android.content.Context
import com.mitsuki.ehit.R
import java.lang.RuntimeException

object GalleryRating {

    val DATA = arrayOf(
//        1 to R.string.text_star_1,
        2 to R.string.text_star_2,
        3 to R.string.text_star_3,
        4 to R.string.text_star_4,
        5 to R.string.text_star_5,
    )

    fun findText(rating: Int): Int {
        for (item in DATA) {
            if (rating == item.first) return item.second
        }
        throw RuntimeException("Error rating")
    }

    fun strList(context: Context): List<String> {
        return DATA.mapIndexed { _, pair ->
            context.getString(pair.second)
        }
    }
}