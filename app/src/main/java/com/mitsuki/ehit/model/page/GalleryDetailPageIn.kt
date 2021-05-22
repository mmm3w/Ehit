package com.mitsuki.ehit.model.page

import java.lang.RuntimeException

class GalleryDetailPageIn {
    companion object {
        const val START = 0
    }

    var targetPage: Int = START
        set(value) {
            if (value < 1) throw  RuntimeException("Value error")
            field = value - 1
        }
}